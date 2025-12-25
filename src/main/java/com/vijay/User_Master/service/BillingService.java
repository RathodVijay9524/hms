package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.*;
import com.vijay.User_Master.entity.Appointment;
import com.vijay.User_Master.entity.Bill;
import com.vijay.User_Master.entity.LabOrder;

import java.util.List;

public interface BillingService {
    // Charge Master
    ChargeItemDto createChargeItem(ChargeItemDto dto);
    List<ChargeItemDto> getAllChargeItems();
    ChargeItemDto updateChargeItem(Long id, ChargeItemDto dto);
    void deleteChargeItem(Long id);

    // Bill Generation
    BillResponse generateBillFromAppointment(Long appointmentId);
    BillResponse getBillById(Long id);
    BillResponse getBillByNumber(String billNumber);
    List<BillResponse> getPatientBills(Long patientId);
    List<BillResponse> getAllBills();

    // Payments
    BillResponse recordPayment(Long billId, PaymentDto paymentDto);
    List<PaymentDto> getBillPayments(Long billId);
    
    // Status Management
    BillResponse cancelBill(Long id, String reason);

    // Statistics
    BillingStatsDto getBillingStats();
}
