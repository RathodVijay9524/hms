package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.dto.pharmacy.DispensingDTO;
import com.vijay.User_Master.dto.pharmacy.DispensingRequest;
import com.vijay.User_Master.service.PharmacyDispensingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacy")
@RequiredArgsConstructor
@Slf4j
public class PharmacyDispensingController {

    private final PharmacyDispensingService dispensingService;

    @GetMapping("/pending-prescriptions")
    public ResponseEntity<?> getPendingPrescriptions() {
        log.info("Request to get pending prescriptions for pharmacy");
        List<DispensingDTO> prescriptions = dispensingService.getPendingPrescriptions();
        return ExceptionUtil.createBuildResponse(prescriptions, HttpStatus.OK);
    }

    @GetMapping("/prescription/{prescriptionId}")
    public ResponseEntity<?> getPrescriptionForDispensing(@PathVariable Long prescriptionId) {
        log.info("Request to get prescription {} for dispensing", prescriptionId);
        DispensingDTO dto = dispensingService.getPrescriptionForDispensing(prescriptionId);
        return ExceptionUtil.createBuildResponse(dto, HttpStatus.OK);
    }

    @PostMapping("/dispense")
    public ResponseEntity<?> dispensePrescription(@RequestBody DispensingRequest request) {
        log.info("Request to dispense prescription {}", request.getPrescriptionId());
        DispensingDTO result = dispensingService.dispensePrescription(request);
        return ExceptionUtil.createBuildResponse(result, HttpStatus.CREATED);
    }

    @GetMapping("/history/{patientId}")
    public ResponseEntity<?> getDispensingHistory(@PathVariable Long patientId) {
        log.info("Request to get dispensing history for patient {}", patientId);
        List<DispensingDTO> history = dispensingService.getDispensingHistory(patientId);
        return ExceptionUtil.createBuildResponse(history, HttpStatus.OK);
    }

    @GetMapping("/all-dispensings")
    public ResponseEntity<?> getAllDispensings() {
        log.info("Request to get all dispensings");
        List<DispensingDTO> dispensings = dispensingService.getAllDispensings();
        return ExceptionUtil.createBuildResponse(dispensings, HttpStatus.OK);
    }

    @DeleteMapping("/{dispensingId}")
    public ResponseEntity<?> cancelDispensing(@PathVariable Long dispensingId,
                                              @RequestParam(required = false) String reason) {
        log.info("Request to cancel dispensing {}", dispensingId);
        dispensingService.cancelDispensing(dispensingId, reason);
        return ExceptionUtil.createBuildResponse("Dispensing cancelled successfully", HttpStatus.OK);
    }

    @GetMapping("/pending-count")
    public ResponseEntity<?> getPendingCount() {
        long count = dispensingService.getPendingCount();
        return ExceptionUtil.createBuildResponse(count, HttpStatus.OK);
    }

    @GetMapping("/download-receipt/{dispensingId}")
    public org.springframework.http.ResponseEntity<byte[]> downloadReceipt(@PathVariable Long dispensingId) {
        java.io.ByteArrayOutputStream bis = dispensingService.getDispensingReceipt(dispensingId);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=dispensing-receipt-" + dispensingId + ".pdf");

        return org.springframework.http.ResponseEntity
                .ok()
                .headers(headers)
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(bis.toByteArray());
    }
}
