package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.BillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class BillingServiceImpl implements BillingService {

    private final ChargeItemRepository chargeItemRepository;
    private final BillRepository billRepository;
    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorVisitRepository doctorVisitRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    private Long getOwnerId() {
        return CommonUtils.getLoggedInUser().getOwnerId();
    }

    private User getOwner() {
        return userRepository.findById(getOwnerId()).orElseThrow();
    }

    // --- Charge Master ---

    @Override
    @Transactional
    public ChargeItemDto createChargeItem(ChargeItemDto dto) {
        ChargeItem item = mapper.map(dto, ChargeItem.class);
        item.setOwner(getOwner());
        ChargeItem saved = chargeItemRepository.save(item);
        return mapper.map(saved, ChargeItemDto.class);
    }

    @Override
    public List<ChargeItemDto> getAllChargeItems() {
        return chargeItemRepository.findByOwnerId(getOwnerId()).stream()
                .map(item -> mapper.map(item, ChargeItemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChargeItemDto updateChargeItem(Long id, ChargeItemDto dto) {
        ChargeItem item = chargeItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChargeItem", "id", id));
        
        item.setName(dto.getName());
        item.setCategory(dto.getCategory());
        item.setBaseAmount(dto.getBaseAmount());
        item.setTaxPercent(dto.getTaxPercent());
        item.setActive(dto.getActive());
        
        return mapper.map(chargeItemRepository.save(item), ChargeItemDto.class);
    }

    @Override
    @Transactional
    public void deleteChargeItem(Long id) {
        chargeItemRepository.deleteById(id);
    }

    // --- Bill Generation ---

    @Override
    @Transactional
    public BillResponse generateBillFromAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

        // Find Consultation Charge for this doctor/hospital
        List<ChargeItem> consultationCharges = chargeItemRepository.findByCategoryAndOwnerId("CONSULTATION", getOwnerId());
        if (consultationCharges.isEmpty()) {
            throw new RuntimeException("No consultation charge configured in Charge Master.");
        }
        ChargeItem consultation = consultationCharges.get(0);

        // Generate Bill Number
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        
        long count = billRepository.countByOwnerIdAndCreatedOnBetween(getOwnerId(), startOfDay, endOfDay) + 1;
        String billNumber = String.format("INV-%s-%03d", dateStr, count);

        Bill bill = Bill.builder()
                .billNumber(billNumber)
                .patient(appointment.getPatient())
                .appointment(appointment)
                .status(Bill.BillStatus.DRAFT)
                .totalAmount(0.0)
                .taxAmount(0.0)
                .discountAmount(0.0)
                .netAmount(0.0)
                .paidAmount(0.0)
                .balanceAmount(0.0)
                .owner(getOwner())
                .build();

        // Add Line Item
        BillItem item = BillItem.builder()
                .bill(bill)
                .itemName(consultation.getName())
                .quantity(1)
                .unitPrice(consultation.getBaseAmount())
                .taxPercent(consultation.getTaxPercent())
                .owner(getOwner())
                .build();
        
        calculateItemTotals(item);
        bill.getItems().add(item);
        recalculateBill(bill);

        Bill saved = billRepository.save(bill);
        return convertToResponse(saved);
    }

    @Override
    @Transactional
    public BillResponse generateBillFromVisit(Long visitId) {
        DoctorVisit visit = doctorVisitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("DoctorVisit", "id", visitId));

        // Find Consultation Charge for this doctor/hospital
        List<ChargeItem> consultationCharges = chargeItemRepository.findByCategoryAndOwnerId("CONSULTATION", getOwnerId());
        if (consultationCharges.isEmpty()) {
            log.warn("No consultation charge configured in Charge Master. Skipping automated bill.");
            return null;
        }
        ChargeItem consultation = consultationCharges.get(0);

        // Generate Bill Number
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        
        long count = billRepository.countByOwnerIdAndCreatedOnBetween(getOwnerId(), startOfDay, endOfDay) + 1;
        String billNumber = String.format("INV-%s-%03d", dateStr, count);

        Bill bill = Bill.builder()
                .billNumber(billNumber)
                .patient(visit.getPatient())
                .appointment(visit.getAppointment())
                .status(Bill.BillStatus.DRAFT)
                .totalAmount(0.0)
                .taxAmount(0.0)
                .discountAmount(0.0)
                .netAmount(0.0)
                .paidAmount(0.0)
                .balanceAmount(0.0)
                .owner(getOwner())
                .build();

        // Add Line Item
        BillItem item = BillItem.builder()
                .bill(bill)
                .itemName(consultation.getName())
                .quantity(1)
                .unitPrice(consultation.getBaseAmount())
                .taxPercent(consultation.getTaxPercent())
                .owner(getOwner())
                .build();
        
        calculateItemTotals(item);
        bill.setItems(new java.util.ArrayList<>(List.of(item)));
        recalculateBill(bill);

        Bill saved = billRepository.save(bill);
        return convertToResponse(saved);
    }

    @Override
    public BillResponse getBillById(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", "id", id));
        return convertToResponse(bill);
    }

    @Override
    public BillResponse getBillByNumber(String billNumber) {
        Bill bill = billRepository.findByBillNumber(billNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", "billNumber", billNumber));
        return convertToResponse(bill);
    }

    @Override
    public List<BillResponse> getPatientBills(Long patientId) {
        return billRepository.findByPatientId(patientId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BillResponse> getAllBills() {
        return billRepository.findByOwnerId(getOwnerId()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // --- Payments ---

    @Override
    @Transactional
    public BillResponse recordPayment(Long billId, PaymentDto paymentDto) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", "id", billId));

        Payment payment = Payment.builder()
                .bill(bill)
                .amount(paymentDto.getAmount())
                .mode(paymentDto.getMode())
                .transactionReference(paymentDto.getTransactionReference())
                .paymentDate(LocalDateTime.now())
                .owner(getOwner())
                .build();

        paymentRepository.save(payment);
        
        bill.setPaidAmount(bill.getPaidAmount() + payment.getAmount());
        updateBillStatus(bill);
        
        Bill saved = billRepository.save(bill);
        return convertToResponse(saved);
    }

    @Override
    public List<PaymentDto> getBillPayments(Long billId) {
        return paymentRepository.findByBillId(billId).stream()
                .map(p -> mapper.map(p, PaymentDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BillResponse cancelBill(Long id, String reason) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", "id", id));
        
        bill.setStatus(Bill.BillStatus.CANCELLED);
        // Logical restoration of any slots/resources if needed
        
        return convertToResponse(billRepository.save(bill));
    }

    @Override
    public BillingStatsDto getBillingStats() {
        Long ownerId = getOwnerId();
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        Double revenue = billRepository.sumTotalRevenue(ownerId);
        Double collected = billRepository.sumTotalCollected(ownerId);
        Double dues = billRepository.sumPendingDues(ownerId);

        return BillingStatsDto.builder()
                .totalRevenue(revenue != null ? revenue : 0.0)
                .totalCollected(collected != null ? collected : 0.0)
                .pendingDues(dues != null ? dues : 0.0)
                .billsToday(billRepository.countByOwnerIdAndCreatedOnBetween(ownerId, startOfDay, endOfDay))
                .partialPaymentsCount(billRepository.countByOwnerIdAndStatus(ownerId, Bill.BillStatus.PARTIALLY_PAID))
                .build();
    }

    // --- Helpers ---

    private void calculateItemTotals(BillItem item) {
        double base = item.getUnitPrice() * item.getQuantity();
        item.setTaxAmount((base * item.getTaxPercent()) / 100.0);
        item.setTotalAmount(base + item.getTaxAmount());
    }

    private void recalculateBill(Bill bill) {
        double total = 0;
        double tax = 0;
        for (BillItem item : bill.getItems()) {
            total += item.getUnitPrice() * item.getQuantity();
            tax += item.getTaxAmount();
        }
        bill.setTotalAmount(total);
        bill.setTaxAmount(tax);
        bill.setNetAmount(total + tax - (bill.getDiscountAmount() != null ? bill.getDiscountAmount() : 0.0));
        bill.setBalanceAmount(bill.getNetAmount() - (bill.getPaidAmount() != null ? bill.getPaidAmount() : 0.0));
    }

    private void updateBillStatus(Bill bill) {
        recalculateBill(bill);
        if (bill.getBalanceAmount() <= 0) {
            bill.setStatus(Bill.BillStatus.PAID);
        } else if (bill.getPaidAmount() > 0) {
            bill.setStatus(Bill.BillStatus.PARTIALLY_PAID);
        } else {
            bill.setStatus(Bill.BillStatus.GENERATED);
        }
    }

    private BillResponse convertToResponse(Bill bill) {
        BillResponse res = mapper.map(bill, BillResponse.class);
        res.setPatientName(bill.getPatient().getName());
        res.setPatientUhid(bill.getPatient().getUhid());
        res.setItems(bill.getItems().stream()
                .map(i -> mapper.map(i, BillItemDto.class))
                .collect(Collectors.toList()));
        return res;
    }
}
