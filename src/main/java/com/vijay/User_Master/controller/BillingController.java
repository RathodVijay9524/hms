package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.dto.BillResponse;
import com.vijay.User_Master.dto.ChargeItemDto;
import com.vijay.User_Master.dto.PaymentDto;
import com.vijay.User_Master.service.BillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
@Log4j2
public class BillingController {

    private final BillingService billingService;

    // --- Charge Master ---

    @PostMapping("/charges")
    public ResponseEntity<?> createChargeItem(@RequestBody ChargeItemDto dto) {
        log.info("Request to create charge item: {}", dto.getName());
        return ExceptionUtil.createBuildResponse(billingService.createChargeItem(dto), HttpStatus.CREATED);
    }

    @GetMapping("/charges")
    public ResponseEntity<?> getAllChargeItems() {
        return ExceptionUtil.createBuildResponse(billingService.getAllChargeItems(), HttpStatus.OK);
    }

    @PutMapping("/charges/{id}")
    public ResponseEntity<?> updateChargeItem(@PathVariable Long id, @RequestBody ChargeItemDto dto) {
        return ExceptionUtil.createBuildResponse(billingService.updateChargeItem(id, dto), HttpStatus.OK);
    }

    @DeleteMapping("/charges/{id}")
    public ResponseEntity<?> deleteChargeItem(@PathVariable Long id) {
        billingService.deleteChargeItem(id);
        return ExceptionUtil.createBuildResponse("Charge item deleted", HttpStatus.OK);
    }

    // --- Bills ---

    @PostMapping("/appointments/{appointmentId}/generate")
    public ResponseEntity<?> generateBill(@PathVariable Long appointmentId) {
        log.info("Request to generate bill for appointment: {}", appointmentId);
        return ExceptionUtil.createBuildResponse(billingService.generateBillFromAppointment(appointmentId), HttpStatus.CREATED);
    }

    @GetMapping("/bills/{id}")
    public ResponseEntity<?> getBill(@PathVariable Long id) {
        return ExceptionUtil.createBuildResponse(billingService.getBillById(id), HttpStatus.OK);
    }

    @GetMapping("/bills/number/{billNumber}")
    public ResponseEntity<?> getBillByNumber(@PathVariable String billNumber) {
        return ExceptionUtil.createBuildResponse(billingService.getBillByNumber(billNumber), HttpStatus.OK);
    }

    @GetMapping("/patients/{patientId}/bills")
    public ResponseEntity<?> getPatientBills(@PathVariable Long patientId) {
        return ExceptionUtil.createBuildResponse(billingService.getPatientBills(patientId), HttpStatus.OK);
    }

    @GetMapping("/bills")
    public ResponseEntity<?> getAllBills() {
        return ExceptionUtil.createBuildResponse(billingService.getAllBills(), HttpStatus.OK);
    }

    // --- Payments ---

    @PostMapping("/bills/{billId}/payments")
    public ResponseEntity<?> recordPayment(@PathVariable Long billId, @RequestBody PaymentDto paymentDto) {
        log.info("Request to record payment for bill: {}", billId);
        return ExceptionUtil.createBuildResponse(billingService.recordPayment(billId, paymentDto), HttpStatus.OK);
    }

    @GetMapping("/bills/{billId}/payments")
    public ResponseEntity<?> getBillPayments(@PathVariable Long billId) {
        return ExceptionUtil.createBuildResponse(billingService.getBillPayments(billId), HttpStatus.OK);
    }

    @PatchMapping("/bills/{id}/cancel")
    public ResponseEntity<?> cancelBill(@PathVariable Long id, @RequestParam String reason) {
        return ExceptionUtil.createBuildResponse(billingService.cancelBill(id, reason), HttpStatus.OK);
    }
}
