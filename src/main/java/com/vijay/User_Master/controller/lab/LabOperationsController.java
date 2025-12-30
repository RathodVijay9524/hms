package com.vijay.User_Master.controller.lab;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lab")
@RequiredArgsConstructor
public class LabOperationsController {

    private final LabInventoryRepository inventoryRepository;
    private final LabMachineRepository machineRepository;
    private final MachineCalibrationLogRepository calibrationLogRepository;
    private final ReferenceLabPartnerRepository partnerRepository;
    private final OutsourcedSampleRepository outsourcedSampleRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    // ======================== INVENTORY HUB ========================

    @GetMapping("/inventory")
    public ResponseEntity<Map<String, Object>> getInventory() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        LocalDate today = LocalDate.now();
        LocalDate expiryThreshold = today.plusDays(30);

        Map<String, Object> response = new HashMap<>();
        
        List<LabInventoryItem> items = inventoryRepository.findByOwnerIdAndIsDeletedFalseOrderByItemNameAsc(ownerId);
        response.put("items", items.stream().map(this::toInventoryDTO).collect(Collectors.toList()));
        
        // Stats
        response.put("totalItems", inventoryRepository.countByOwnerIdAndIsDeletedFalse(ownerId));
        response.put("lowStockCount", inventoryRepository.countLowStock(ownerId));
        response.put("expiringSoonCount", inventoryRepository.countExpiringSoon(ownerId, today, expiryThreshold));
        
        BigDecimal valuation = inventoryRepository.getTotalValuation(ownerId);
        response.put("totalValuation", valuation != null ? valuation : BigDecimal.ZERO);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/inventory")
    public ResponseEntity<Map<String, Object>> addInventoryItem(@RequestBody Map<String, Object> request) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        User owner = userRepository.findById(ownerId).orElseThrow();

        LabInventoryItem item = LabInventoryItem.builder()
                .itemName((String) request.get("itemName"))
                .manufacturer((String) request.get("manufacturer"))
                .category((String) request.get("category"))
                .batchNumber((String) request.get("batchNumber"))
                .currentStock(Integer.parseInt(request.get("currentStock").toString()))
                .reorderLevel(request.get("reorderLevel") != null ? Integer.parseInt(request.get("reorderLevel").toString()) : null)
                .maxStock(request.get("maxStock") != null ? Integer.parseInt(request.get("maxStock").toString()) : null)
                .unit((String) request.get("unit"))
                .unitPrice(request.get("unitPrice") != null ? new BigDecimal(request.get("unitPrice").toString()) : null)
                .expiryDate(request.get("expiryDate") != null ? LocalDate.parse(request.get("expiryDate").toString()) : null)
                .lastRestockedDate(LocalDate.now())
                .owner(owner)
                .build();

        inventoryRepository.save(item);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Inventory item added successfully");
        response.put("id", item.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/inventory/{id}")
    public ResponseEntity<Map<String, Object>> updateInventoryItem(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();

        LabInventoryItem item = inventoryRepository.findById(id)
                .filter(i -> i.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (request.containsKey("currentStock")) {
            item.setCurrentStock(Integer.parseInt(request.get("currentStock").toString()));
            item.setLastRestockedDate(LocalDate.now());
        }
        if (request.containsKey("batchNumber")) item.setBatchNumber((String) request.get("batchNumber"));
        if (request.containsKey("expiryDate")) item.setExpiryDate(LocalDate.parse(request.get("expiryDate").toString()));

        inventoryRepository.save(item);

        return ResponseEntity.ok(Map.of("success", true, "message", "Item updated"));
    }

    // ======================== QC & MACHINE LOGS ========================

    @GetMapping("/machines")
    public ResponseEntity<Map<String, Object>> getMachines() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();

        Map<String, Object> response = new HashMap<>();
        
        List<LabMachine> machines = machineRepository.findByOwnerIdAndIsDeletedFalseOrderByMachineNameAsc(ownerId);
        response.put("machines", machines.stream().map(this::toMachineDTO).collect(Collectors.toList()));
        
        // Recent calibration logs
        List<MachineCalibrationLog> logs = calibrationLogRepository
                .findByOwnerIdAndIsDeletedFalseOrderByCalibrationDateDesc(ownerId, PageRequest.of(0, 10));
        response.put("recentLogs", logs.stream().map(this::toLogDTO).collect(Collectors.toList()));

        response.put("onlineCount", machineRepository.countByOwnerIdAndIsDeletedFalseAndStatus(ownerId, LabMachine.MachineStatus.ONLINE));
        response.put("offlineCount", machineRepository.countByOwnerIdAndIsDeletedFalseAndStatus(ownerId, LabMachine.MachineStatus.OFFLINE));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/machines")
    public ResponseEntity<Map<String, Object>> addMachine(@RequestBody Map<String, Object> request) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        User owner = userRepository.findById(ownerId).orElseThrow();

        LabMachine machine = LabMachine.builder()
                .machineName((String) request.get("machineName"))
                .manufacturer((String) request.get("manufacturer"))
                .modelNumber((String) request.get("modelNumber"))
                .serialNumber((String) request.get("serialNumber"))
                .machineType((String) request.get("machineType"))
                .location((String) request.get("location"))
                .calibrationIntervalDays(request.get("calibrationIntervalDays") != null ? 
                    Integer.parseInt(request.get("calibrationIntervalDays").toString()) : 7)
                .status(LabMachine.MachineStatus.ONLINE)
                .owner(owner)
                .build();

        machineRepository.save(machine);

        return ResponseEntity.ok(Map.of("success", true, "id", machine.getId()));
    }

    @PostMapping("/machines/{id}/calibration")
    public ResponseEntity<Map<String, Object>> logCalibration(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        User owner = userRepository.findById(ownerId).orElseThrow();

        LabMachine machine = machineRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Machine not found"));

        MachineCalibrationLog log = MachineCalibrationLog.builder()
                .machine(machine)
                .calibrationDate(LocalDateTime.now())
                .calibrationType(MachineCalibrationLog.CalibrationType.valueOf((String) request.getOrDefault("type", "DAILY_QC")))
                .result(MachineCalibrationLog.CalibrationResult.valueOf((String) request.getOrDefault("result", "PASS")))
                .notes((String) request.get("notes"))
                .performedBy((String) request.get("performedBy"))
                .parametersChecked(request.get("parametersChecked") != null ? Integer.parseInt(request.get("parametersChecked").toString()) : null)
                .parametersInRange(request.get("parametersInRange") != null ? Integer.parseInt(request.get("parametersInRange").toString()) : null)
                .owner(owner)
                .build();

        calibrationLogRepository.save(log);

        // Update machine calibration dates
        machine.setLastCalibrationDate(LocalDateTime.now());
        if (machine.getCalibrationIntervalDays() != null) {
            machine.setNextCalibrationDue(LocalDateTime.now().plusDays(machine.getCalibrationIntervalDays()));
        }
        machineRepository.save(machine);

        return ResponseEntity.ok(Map.of("success", true, "message", "Calibration logged"));
    }

    @PutMapping("/machines/{id}/status")
    public ResponseEntity<Map<String, Object>> updateMachineStatus(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();

        LabMachine machine = machineRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Machine not found"));

        machine.setStatus(LabMachine.MachineStatus.valueOf((String) request.get("status")));
        if (request.containsKey("errorCode")) machine.setErrorCode((String) request.get("errorCode"));
        if (request.containsKey("errorDescription")) machine.setErrorDescription((String) request.get("errorDescription"));

        machineRepository.save(machine);

        return ResponseEntity.ok(Map.of("success", true));
    }

    // ======================== REFERENCE HUB ========================

    @GetMapping("/reference/partners")
    public ResponseEntity<List<Map<String, Object>>> getPartners() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();

        List<ReferenceLabPartner> partners = partnerRepository.findByOwnerIdAndIsDeletedFalseOrderByPartnerNameAsc(ownerId);
        
        return ResponseEntity.ok(partners.stream().map(p -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", p.getId());
            dto.put("partnerName", p.getPartnerName());
            dto.put("shortCode", p.getShortCode());
            dto.put("specialization", p.getSpecialization());
            dto.put("contactPerson", p.getContactPerson());
            dto.put("contactPhone", p.getContactPhone());
            dto.put("isActive", p.getIsActive());
            dto.put("activeSamples", outsourcedSampleRepository.countActiveByPartner(ownerId, p.getId()));
            return dto;
        }).collect(Collectors.toList()));
    }

    @PostMapping("/reference/partners")
    public ResponseEntity<Map<String, Object>> addPartner(@RequestBody Map<String, Object> request) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        User owner = userRepository.findById(ownerId).orElseThrow();

        ReferenceLabPartner partner = ReferenceLabPartner.builder()
                .partnerName((String) request.get("partnerName"))
                .shortCode((String) request.get("shortCode"))
                .specialization((String) request.get("specialization"))
                .contactPerson((String) request.get("contactPerson"))
                .contactPhone((String) request.get("contactPhone"))
                .contactEmail((String) request.get("contactEmail"))
                .address((String) request.get("address"))
                .portalUrl((String) request.get("portalUrl"))
                .owner(owner)
                .build();

        partnerRepository.save(partner);

        return ResponseEntity.ok(Map.of("success", true, "id", partner.getId()));
    }

    @GetMapping("/reference/samples")
    public ResponseEntity<Map<String, Object>> getOutsourcedSamples() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();

        Map<String, Object> response = new HashMap<>();
        
        List<OutsourcedSample> samples = outsourcedSampleRepository.findByOwnerIdAndIsDeletedFalseOrderBySentDateTimeDesc(ownerId);
        response.put("samples", samples.stream().map(this::toSampleDTO).collect(Collectors.toList()));
        response.put("totalActive", outsourcedSampleRepository.countActive(ownerId));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reference/samples")
    public ResponseEntity<Map<String, Object>> createOutsourcedSample(@RequestBody Map<String, Object> request) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        User owner = userRepository.findById(ownerId).orElseThrow();

        Patient patient = patientRepository.findById(Long.parseLong(request.get("patientId").toString()))
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        ReferenceLabPartner partner = partnerRepository.findByIdAndOwnerId(
                Long.parseLong(request.get("partnerId").toString()), ownerId)
                .orElseThrow(() -> new RuntimeException("Partner not found"));

        String sampleId = "SAM-" + System.currentTimeMillis() % 100000;

        OutsourcedSample sample = OutsourcedSample.builder()
                .sampleId(sampleId)
                .patient(patient)
                .partner(partner)
                .testName((String) request.get("testName"))
                .testCategory((String) request.get("testCategory"))
                .status(OutsourcedSample.SampleStatus.PENDING)
                .notes((String) request.get("notes"))
                .owner(owner)
                .build();

        outsourcedSampleRepository.save(sample);

        return ResponseEntity.ok(Map.of("success", true, "sampleId", sampleId));
    }

    @PutMapping("/reference/samples/{id}/status")
    public ResponseEntity<Map<String, Object>> updateSampleStatus(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();

        OutsourcedSample sample = outsourcedSampleRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new RuntimeException("Sample not found"));

        OutsourcedSample.SampleStatus newStatus = OutsourcedSample.SampleStatus.valueOf((String) request.get("status"));
        sample.setStatus(newStatus);

        switch (newStatus) {
            case IN_TRANSIT -> sample.setSentDateTime(LocalDateTime.now());
            case RECEIVED -> sample.setReceivedByPartnerDateTime(LocalDateTime.now());
            case COMPLETED -> sample.setResultReceivedDateTime(LocalDateTime.now());
        }

        if (request.containsKey("trackingNumber")) sample.setTrackingNumber((String) request.get("trackingNumber"));
        if (request.containsKey("courierName")) sample.setCourierName((String) request.get("courierName"));

        outsourcedSampleRepository.save(sample);

        return ResponseEntity.ok(Map.of("success", true));
    }

    // ======================== DTO MAPPINGS ========================

    private Map<String, Object> toInventoryDTO(LabInventoryItem item) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", item.getId());
        dto.put("itemName", item.getItemName());
        dto.put("manufacturer", item.getManufacturer());
        dto.put("category", item.getCategory());
        dto.put("batchNumber", item.getBatchNumber());
        dto.put("currentStock", item.getCurrentStock());
        dto.put("reorderLevel", item.getReorderLevel());
        dto.put("maxStock", item.getMaxStock());
        dto.put("unit", item.getUnit());
        dto.put("unitPrice", item.getUnitPrice());
        dto.put("expiryDate", item.getExpiryDate());
        dto.put("stockStatus", item.getStockStatus().name());
        
        // Calculate stock percentage
        if (item.getMaxStock() != null && item.getMaxStock() > 0) {
            dto.put("stockPercentage", (item.getCurrentStock() * 100) / item.getMaxStock());
        } else {
            dto.put("stockPercentage", item.getCurrentStock() > 0 ? 100 : 0);
        }
        return dto;
    }

    private Map<String, Object> toMachineDTO(LabMachine machine) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", machine.getId());
        dto.put("machineName", machine.getMachineName());
        dto.put("manufacturer", machine.getManufacturer());
        dto.put("machineType", machine.getMachineType());
        dto.put("status", machine.getStatus().name());
        dto.put("lastCalibrationDate", machine.getLastCalibrationDate());
        dto.put("nextCalibrationDue", machine.getNextCalibrationDue());
        dto.put("errorCode", machine.getErrorCode());
        dto.put("errorDescription", machine.getErrorDescription());
        return dto;
    }

    private Map<String, Object> toLogDTO(MachineCalibrationLog log) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", log.getId());
        dto.put("machineName", log.getMachine().getMachineName());
        dto.put("machineId", log.getMachine().getId());
        dto.put("calibrationDate", log.getCalibrationDate());
        dto.put("calibrationType", log.getCalibrationType().name());
        dto.put("result", log.getResult().name());
        dto.put("notes", log.getNotes());
        dto.put("parametersChecked", log.getParametersChecked());
        dto.put("parametersInRange", log.getParametersInRange());
        return dto;
    }

    private Map<String, Object> toSampleDTO(OutsourcedSample sample) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", sample.getId());
        dto.put("sampleId", sample.getSampleId());
        dto.put("patientName", sample.getPatient().getName());
        dto.put("patientUhid", sample.getPatient().getUhid());
        dto.put("patientId", sample.getPatient().getId());
        dto.put("testName", sample.getTestName());
        dto.put("testCategory", sample.getTestCategory());
        dto.put("partnerName", sample.getPartner().getPartnerName());
        dto.put("partnerShortCode", sample.getPartner().getShortCode());
        dto.put("partnerId", sample.getPartner().getId());
        dto.put("status", sample.getStatus().name());
        dto.put("sentDateTime", sample.getSentDateTime());
        dto.put("trackingNumber", sample.getTrackingNumber());
        return dto;
    }
}
