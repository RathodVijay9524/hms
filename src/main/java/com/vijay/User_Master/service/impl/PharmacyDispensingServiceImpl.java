package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.pharmacy.*;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.exceptions.BadApiRequestException;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.PdfExportService;
import com.vijay.User_Master.service.PharmacyDispensingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PharmacyDispensingServiceImpl implements PharmacyDispensingService {

    private final PrescriptionDispensingRepository dispensingRepository;
    private final DispensedItemRepository dispensedItemRepository;
    private final PdfExportService pdfExportService;
    private final PrescriptionRepository prescriptionRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final StockAlertRepository stockAlertRepository;
    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<DispensingDTO> getPendingPrescriptions() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        
        // Get all prescriptions that don't have a dispensing record yet
        List<Prescription> prescriptions = prescriptionRepository.findByOwnerId(ownerId);
        
        return prescriptions.stream()
                .filter(p -> !hasDispensing(p.getId()))
                .map(this::convertToDispensingDTO)
                .collect(Collectors.toList());
    }

    private boolean hasDispensing(Long prescriptionId) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return dispensingRepository.findByPrescriptionIdAndOwnerId(prescriptionId, ownerId).isPresent();
    }

    @Override
    public DispensingDTO getPrescriptionForDispensing(Long prescriptionId) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        
        Prescription prescription = prescriptionRepository.findByIdAndOwnerId(prescriptionId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription", "ID", prescriptionId));
        
        DispensingDTO dto = convertToDispensingDTO(prescription);
        
        // Add stock availability info
        for (DispensedItemDTO item : dto.getItems()) {
            InventoryItem inventoryItem = findInventoryItemForMedicine(item.getMedicineName(), ownerId);
            if (inventoryItem != null) {
                item.setInventoryItemId(inventoryItem.getId());
                item.setQuantityAvailable(inventoryItem.getCurrentStock());
                item.setInStock(inventoryItem.getCurrentStock() >= item.getQuantityPrescribed());
                item.setUnitPrice(inventoryItem.getUnitPrice());
            } else {
                item.setInStock(false);
                item.setQuantityAvailable(0);
            }
        }
        
        return dto;
    }

    private InventoryItem findInventoryItemForMedicine(String medicineName, Long ownerId) {
        if (medicineName == null || medicineName.trim().isEmpty()) return null;
        String trimmedName = medicineName.trim();
        
        // Use repository method for direct lookup
        return inventoryItemRepository.findByNameAndOwnerIdAndIsDeletedFalse(trimmedName, ownerId)
                .orElseGet(() -> {
                    // Fallback to case-insensitive stream filter if exact match fails
                    List<InventoryItem> items = inventoryItemRepository.findByOwnerIdAndIsDeletedFalse(ownerId);
                    return items.stream()
                            .filter(item -> item.getName().trim().equalsIgnoreCase(trimmedName))
                            .findFirst()
                            .orElse(null);
                });
    }

    @Override
    @Transactional
    public DispensingDTO dispensePrescription(DispensingRequest request) {
        log.info("Processing dispensing for prescription ID: {}", request.getPrescriptionId());
        
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", ownerId));

        User currentUser = userRepository.findById(CommonUtils.getLoggedInUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", CommonUtils.getLoggedInUser().getId()));
        
        Prescription prescription = prescriptionRepository.findByIdAndOwnerId(request.getPrescriptionId(), ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription", "ID", request.getPrescriptionId()));
        
        // Check if already dispensed
        if (hasDispensing(request.getPrescriptionId())) {
            throw new BadApiRequestException("This prescription has already been dispensed");
        }
        
        // Create dispensing record
        PrescriptionDispensing dispensing = PrescriptionDispensing.builder()
                .prescription(prescription)
                .dispensedBy(currentUser)
                .dispensedDate(LocalDateTime.now())
                .status(PrescriptionDispensing.DispensingStatus.IN_PROGRESS)
                .notes(request.getNotes())
                .owner(owner)
                .build();
        
        // Process each item
        int totalItems = request.getItems().size();
        int dispensedCount = 0;
        
        for (DispensingItemRequest itemReq : request.getItems()) {
            if (itemReq.getQuantityDispensed() > 0) {
                DispensedItem dispensedItem = createDispensedItem(itemReq, dispensing);
                dispensedItem.calculateTotal(); // Explicitly calculate before parent total
                dispensing.addDispensedItem(dispensedItem);
                
                // Reduce inventory stock
                reduceInventoryStock(itemReq.getInventoryItemId(), itemReq.getQuantityDispensed(), ownerId, dispensing.getId());
                
                dispensedCount++;
            }
        }
        
        // Determine final status
        if (dispensedCount == 0) {
            throw new BadApiRequestException("No items were dispensed");
        } else if (dispensedCount < totalItems) {
            dispensing.setStatus(PrescriptionDispensing.DispensingStatus.PARTIAL);
        } else {
            dispensing.setStatus(PrescriptionDispensing.DispensingStatus.COMPLETED);
        }
        
        // Calculate total
        dispensing.calculateTotal();
        
        PrescriptionDispensing saved = dispensingRepository.save(dispensing);
        log.info("Dispensing completed with status: {}", saved.getStatus());
        
        return convertToFullDispensingDTO(saved);
    }

    private DispensedItem createDispensedItem(DispensingItemRequest request, PrescriptionDispensing dispensing) {
        Long ownerId = dispensing.getOwner().getId();
        InventoryItem inventoryItem = inventoryItemRepository.findByIdAndOwnerId(request.getInventoryItemId(), ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("InventoryItem", "ID", request.getInventoryItemId()));
        
        if (Boolean.TRUE.equals(inventoryItem.getIsDeleted()) || !Boolean.TRUE.equals(inventoryItem.getIsActive())) {
            throw new BadApiRequestException("Inventory item is not active");
        }
        
        if (request.getQuantityDispensed() == null || request.getQuantityDispensed() <= 0) {
            throw new BadApiRequestException("Invalid quantity to dispense");
        }
        
        if (inventoryItem.getCurrentStock() != null && inventoryItem.getCurrentStock() < request.getQuantityDispensed()) {
            throw new BadApiRequestException("Insufficient stock for " + inventoryItem.getName());
        }
        
        BigDecimal unitPrice = request.getUnitPrice();
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) == 0) {
            unitPrice = inventoryItem.getUnitPrice() != null ? inventoryItem.getUnitPrice() : BigDecimal.ZERO;
        }

        return DispensedItem.builder()
                .dispensing(dispensing)
                .inventoryItem(inventoryItem)
                .medicineName(inventoryItem.getName()) // Store medicine name
                .quantityDispensed(request.getQuantityDispensed())
                .unitPrice(unitPrice)
                .batchNumber(request.getBatchNumber())
                .expiryDate(request.getExpiryDate())
                .notes(request.getNotes())
                .owner(dispensing.getOwner())
                .build();
    }

    private void reduceInventoryStock(Long inventoryItemId, Integer quantity, Long ownerId, Long dispensingId) {
      if (quantity == null || quantity <= 0) {
        throw new BadApiRequestException("Invalid stock deduction quantity");
      }

      InventoryItem item = inventoryItemRepository.findByIdAndOwnerId(inventoryItemId, ownerId)
              .orElseThrow(() -> new ResourceNotFoundException("InventoryItem", "ID", inventoryItemId));

      if (Boolean.TRUE.equals(item.getIsDeleted()) || !Boolean.TRUE.equals(item.getIsActive())) {
        throw new BadApiRequestException("Inventory item is not active");
      }

      int before = item.getCurrentStock() != null ? item.getCurrentStock() : 0;
      if (before < quantity) {
        throw new BadApiRequestException("Insufficient stock for " + item.getName() + " (available " + before + ")");
      }
      int after = before - quantity;

      item.setCurrentStock(after);
      inventoryItemRepository.save(item);

      StockMovement movement = new StockMovement();
      movement.setInventoryItem(item);
      movement.setMovementType(StockMovement.MovementType.OUT);
      movement.setQuantityChange(-quantity);
      movement.setQuantityBefore(before);
      movement.setQuantityAfter(after);
      movement.setReferenceType("PRESCRIPTION_DISPENSING");
      movement.setReferenceId(dispensingId);
      movement.setNotes("Dispensed");
      movement.setPerformedBy(CommonUtils.getLoggedInUser().getId());
      movement.setOwner(item.getOwner()); // Set owner from item
      stockMovementRepository.save(movement);

      syncStockAlert(item, ownerId);
      log.info("Reduced stock for {}: {} units", item.getName(), quantity);
    }

    private void restoreInventoryStock(Long inventoryItemId, Integer quantity, Long ownerId, Long dispensingId) {
      if (quantity == null || quantity <= 0) return;

      InventoryItem item = inventoryItemRepository.findByIdAndOwnerId(inventoryItemId, ownerId)
              .orElseThrow(() -> new ResourceNotFoundException("InventoryItem", "ID", inventoryItemId));

      int before = item.getCurrentStock() != null ? item.getCurrentStock() : 0;
      int after = before + quantity;

      item.setCurrentStock(after);
      inventoryItemRepository.save(item);

      StockMovement movement = new StockMovement();
      movement.setInventoryItem(item);
      movement.setMovementType(StockMovement.MovementType.IN);
      movement.setQuantityChange(quantity);
      movement.setQuantityBefore(before);
      movement.setQuantityAfter(after);
      movement.setReferenceType("PRESCRIPTION_DISPENSING_CANCEL");
      movement.setReferenceId(dispensingId);
      movement.setNotes("Reverted due to cancellation");
      movement.setPerformedBy(CommonUtils.getLoggedInUser().getId());
      movement.setOwner(item.getOwner()); // Set owner from item
      stockMovementRepository.save(movement);

      syncStockAlert(item, ownerId);
    }

    private void syncStockAlert(InventoryItem item, Long ownerId) {
      if (item == null) return;
      int current = item.getCurrentStock() != null ? item.getCurrentStock() : 0;

      StockAlert.AlertType type;
      int threshold;
      if (current <= 0) {
        type = StockAlert.AlertType.OUT_OF_STOCK;
        threshold = 0;
      } else if (item.getMinStockLevel() != null && current <= item.getMinStockLevel()) {
        type = StockAlert.AlertType.LOW_STOCK;
        threshold = item.getMinStockLevel();
      } else {
        List<StockAlert> open = stockAlertRepository.findByInventoryItemIdAndOwnerIdAndIsResolvedFalse(item.getId(), ownerId);
        if (open != null && !open.isEmpty()) {
          open.forEach(a -> {
            a.setIsResolved(true);
            a.setResolvedDate(LocalDateTime.now());
            a.setResolvedBy(CommonUtils.getLoggedInUser().getId());
          });
          stockAlertRepository.saveAll(open);
        }
        return;
      }

      if (stockAlertRepository.findFirstByInventoryItemIdAndOwnerIdAndIsResolvedFalseAndAlertType(item.getId(), ownerId, type).isPresent()) {
        return;
      }

      StockAlert alert = new StockAlert();
      alert.setInventoryItem(item);
      alert.setAlertType(type);
      alert.setCurrentStock(current);
      alert.setThresholdLevel(threshold);
      alert.setIsResolved(false); // Keep this line as @Builder.Default cannot be used here
      alert.setNotes("Auto-generated");
      alert.setOwner(item.getOwner()); // Set owner from item
      stockAlertRepository.save(alert);
    }

    @Override
    public List<DispensingDTO> getDispensingHistory(Long patientId) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return dispensingRepository.findByPatientId(patientId, ownerId).stream()
                .map(this::convertToFullDispensingDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DispensingDTO> getAllDispensings() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return dispensingRepository.findByOwnerIdOrderByCreatedOnDesc(ownerId).stream()
                .map(this::convertToFullDispensingDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelDispensing(Long dispensingId, String reason) {
      Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
      PrescriptionDispensing dispensing = dispensingRepository.findById(dispensingId)
              .orElseThrow(() -> new ResourceNotFoundException("Dispensing", "ID", dispensingId));

      if (dispensing.getOwner() == null || dispensing.getOwner().getId() == null || !dispensing.getOwner().getId().equals(ownerId)) {
        throw new BadApiRequestException("Unauthorized dispensing access");
      }

      if (dispensing.getStatus() == PrescriptionDispensing.DispensingStatus.CANCELLED) {
        return;
      }

      List<DispensedItem> items = dispensing.getDispensedItems() != null ? dispensing.getDispensedItems() : new ArrayList<>();
      for (DispensedItem di : items) {
        if (di.getInventoryItem() != null && di.getInventoryItem().getId() != null) {
          restoreInventoryStock(di.getInventoryItem().getId(), di.getQuantityDispensed(), ownerId, dispensing.getId());
        }
      }

      dispensing.setStatus(PrescriptionDispensing.DispensingStatus.CANCELLED);
      String oldNotes = dispensing.getNotes() != null ? dispensing.getNotes() : "";
      dispensing.setNotes(oldNotes + "\nCancelled: " + (reason != null ? reason : ""));
      dispensingRepository.save(dispensing);
    }

    @Override
    public PharmacyDashboardDTO getDashboardStats() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        LocalDateTime todayStart = LocalDateTime.now().with(java.time.LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.now().with(java.time.LocalTime.MAX);
        LocalDateTime expiringThreshold = LocalDateTime.now().plusDays(90);

        // 1. Pending Prescriptions Count
        long pendingCount = getPendingCount();

        // 2. Low Stock Count
        long lowStockCount = inventoryItemRepository.getLowStockCount(ownerId);

        // 3. Today Dispensed Count
        long todayDispensed = dispensingRepository.findByDispensedDateBetweenAndOwnerId(todayStart, todayEnd, ownerId).size();

        // 4. Expiring Soon Count (Dummy logic for now based on current entities if they have expiry date)
        // Note: DispensedItem has expiryDate, but InventoryItem might not have it directly in its core entity based on my research.
        // I'll check InventoryItem again. If it doesn't have it, I'll return 0 or implement a basic check if possible.
        long expiringSoon = 0; // Placeholder

        // 5. Recent Pending Prescriptions
        List<DispensingDTO> recentPrescriptions = getPendingPrescriptions().stream()
                .limit(5)
                .collect(Collectors.toList());

        // 6. Recent Completed Dispensings
        List<DispensingDTO> recentDispensings = dispensingRepository.findByOwnerIdOrderByCreatedOnDesc(ownerId).stream()
                .limit(5)
                .map(this::convertToFullDispensingDTO)
                .collect(Collectors.toList());

        // 7. Stock Alerts
        List<PharmacyDashboardDTO.StockAlertDTO> stockAlerts = stockAlertRepository.findByOwnerIdAndIsResolvedFalse(ownerId).stream()
                .limit(5)
                .map(alert -> PharmacyDashboardDTO.StockAlertDTO.builder()
                        .medicineName(alert.getInventoryItem().getName())
                        .sku(alert.getInventoryItem().getItemCode())
                        .currentStock(alert.getCurrentStock())
                        .minStock(alert.getThresholdLevel())
                        .type(alert.getAlertType().toString())
                        .build())
                .collect(Collectors.toList());

        return PharmacyDashboardDTO.builder()
                .pendingPrescriptionsCount(pendingCount)
                .lowStockCount(lowStockCount)
                .todayDispensedCount(todayDispensed)
                .expiringSoonCount(expiringSoon)
                .recentPrescriptions(recentPrescriptions)
                .recentDispensings(recentDispensings)
                .stockAlerts(stockAlerts)
                .build();
    }

    @Override
    public long getPendingCount() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return prescriptionRepository.findByOwnerId(ownerId).stream()
                .filter(p -> !hasDispensing(p.getId()))
                .count();
    }

    @Override
    public java.io.ByteArrayOutputStream getDispensingReceipt(Long dispensingId) {
        return pdfExportService.generateDispensingReceipt(dispensingId);
    }

    private DispensingDTO convertToDispensingDTO(Prescription prescription) {
        User doctor = prescription.getVisit() != null ? prescription.getVisit().getDoctor() : null;
        Patient patient = prescription.getVisit() != null ? prescription.getVisit().getPatient() : null;
        Long ownerId = prescription.getOwner().getId();
        
        return DispensingDTO.builder()
                .prescriptionId(prescription.getId())
                .patientName(patient != null ? patient.getName() : "Unknown")
                .patientUhid(patient != null ? patient.getUhid() : "N/A")
                .patientAge(calculateAge(patient != null ? patient.getDateOfBirth() : null))
                .patientGender(patient != null && patient.getGender() != null ? patient.getGender().toString() : "N/A")
                .patientPhone(patient != null ? patient.getPhone() : "N/A")
                .doctorName(doctor != null ? doctor.getName() : "N/A")
                .prescriptionDate(convertToLocalDateTime(prescription.getCreatedOn()))
                .status("PENDING")
                .items(prescription.getMedications().stream()
                        .map(medication -> {
                            Optional<InventoryItem> itemOpt = inventoryItemRepository.findByNameAndOwnerIdAndIsDeletedFalse(medication.getMedicineName(), ownerId);
                            return DispensedItemDTO.builder()
                                    .inventoryItemId(itemOpt.map(InventoryItem::getId).orElse(null))
                                    .medicineName(medication.getMedicineName())
                                    .quantityPrescribed(medication.getQuantity() != null ? medication.getQuantity() : 0)
                                    .dosage(medication.getDosage())
                                    .duration(medication.getDuration())
                                    .instructions(medication.getInstructions())
                                    .unitPrice(itemOpt.map(InventoryItem::getUnitPrice).orElse(java.math.BigDecimal.ZERO))
                                    .inStock(itemOpt.map(i -> i.getCurrentStock() > 0).orElse(false))
                                    .quantityAvailable(itemOpt.map(InventoryItem::getCurrentStock).orElse(0))
                                    .build();
                        })
                        .collect(Collectors.toList()))
                .build();
    }

    private String calculateAge(java.time.LocalDate dob) {
        if (dob == null) return "N/A";
        return java.time.Period.between(dob, java.time.LocalDate.now()).getYears() + "y";
    }

    private DispensingDTO convertToFullDispensingDTO(PrescriptionDispensing dispensing) {
        User doctor = dispensing.getPrescription().getVisit() != null 
                ? dispensing.getPrescription().getVisit().getDoctor() : null;
        
        return DispensingDTO.builder()
                .id(dispensing.getId())
                .prescriptionId(dispensing.getPrescription().getId())
                .patientName(dispensing.getPrescription().getVisit().getPatient().getName())
                .patientUhid(dispensing.getPrescription().getVisit().getPatient().getUhid())
                .doctorName(doctor != null ? doctor.getName() : "N/A")
                .prescriptionDate(convertToLocalDateTime(dispensing.getPrescription().getCreatedOn()))
                .dispensedDate(dispensing.getDispensedDate())
                .dispensedBy(dispensing.getDispensedBy().getName())
                .status(dispensing.getStatus().toString())
                .totalAmount(dispensing.getTotalAmount())
                .notes(dispensing.getNotes())
                .items(dispensing.getDispensedItems().stream()
                        .map(item -> DispensedItemDTO.builder()
                                .id(item.getId())
                                .inventoryItemId(item.getInventoryItem().getId())
                                .medicineName(item.getMedicineName())
                                .quantityDispensed(item.getQuantityDispensed())
                                .unitPrice(item.getUnitPrice())
                                .totalPrice(item.getTotalPrice())
                                .batchNumber(item.getBatchNumber())
                                .expiryDate(item.getExpiryDate())
                                .notes(item.getNotes())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private java.time.LocalDateTime convertToLocalDateTime(java.util.Date date) {
        if (date == null) return null;
        return date.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
