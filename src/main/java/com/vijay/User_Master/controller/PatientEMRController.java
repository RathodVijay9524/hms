package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.emr.*;
import com.vijay.User_Master.service.PatientEMRService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emr")
@RequiredArgsConstructor
public class PatientEMRController {

    private final PatientEMRService patientEMRService;

    // Medical History
    @GetMapping("/patients/{patientId}/history")
    public ResponseEntity<MedicalHistoryDTO> getMedicalHistory(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientEMRService.getMedicalHistory(patientId));
    }

    @PostMapping("/patients/{patientId}/history")
    public ResponseEntity<MedicalHistoryDTO> saveMedicalHistory(
            @PathVariable Long patientId,
            @RequestBody MedicalHistoryDTO historyDTO) {
        return ResponseEntity.ok(patientEMRService.saveMedicalHistory(patientId, historyDTO));
    }

    // Vitals
    @PostMapping("/patients/{patientId}/vitals")
    public ResponseEntity<VitalSignDTO> addVitalSign(
            @PathVariable Long patientId,
            @RequestBody VitalSignDTO vitalSignDTO) {
        return ResponseEntity.ok(patientEMRService.addVitalSign(patientId, vitalSignDTO));
    }

    @GetMapping("/patients/{patientId}/vitals")
    public ResponseEntity<Page<VitalSignDTO>> getVitalSigns(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(patientEMRService.getVitalSigns(patientId, page, size));
    }

    @GetMapping("/patients/{patientId}/vitals/recent")
    public ResponseEntity<List<VitalSignDTO>> getRecentVitalSigns(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientEMRService.getRecentVitalSigns(patientId));
    }

    // Doctor Visits
    @PostMapping("/patients/{patientId}/visits")
    public ResponseEntity<DoctorVisitDTO> createVisit(
            @PathVariable Long patientId,
            @RequestBody DoctorVisitDTO visitDTO) {
        return ResponseEntity.ok(patientEMRService.createVisit(patientId, visitDTO));
    }

    @GetMapping("/patients/{patientId}/visits")
    public ResponseEntity<Page<DoctorVisitDTO>> getPatientVisits(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(patientEMRService.getPatientVisits(patientId, page, size));
    }

    @GetMapping("/visits/{visitId}")
    public ResponseEntity<DoctorVisitDTO> getVisitDetails(@PathVariable Long visitId) {
        return ResponseEntity.ok(patientEMRService.getVisitDetails(visitId));
    }

    // Visit Lifecycle Management
    @PostMapping("/visits/{visitId}/start")
    public ResponseEntity<DoctorVisitDTO> startVisit(@PathVariable Long visitId) {
        return ResponseEntity.ok(patientEMRService.startVisit(visitId));
    }

    @PostMapping("/visits/{visitId}/close")
    public ResponseEntity<DoctorVisitDTO> closeVisit(@PathVariable Long visitId) {
        return ResponseEntity.ok(patientEMRService.closeVisit(visitId));
    }

    @PostMapping("/visits/{visitId}/lock")
    public ResponseEntity<DoctorVisitDTO> lockVisit(@PathVariable Long visitId) {
        return ResponseEntity.ok(patientEMRService.lockVisit(visitId));
    }

    // Prescriptions
    @PostMapping("/visits/{visitId}/prescription")
    public ResponseEntity<PrescriptionDTO> addPrescription(
            @PathVariable Long visitId,
            @RequestBody PrescriptionDTO prescriptionDTO) {
        return ResponseEntity.ok(patientEMRService.addPrescription(visitId, prescriptionDTO));
    }

    @GetMapping("/visits/{visitId}/prescription")
    public ResponseEntity<PrescriptionDTO> getPrescription(@PathVariable Long visitId) {
        return ResponseEntity.ok(patientEMRService.getPrescriptionByVisit(visitId));
    }

    @GetMapping("/patients/{patientId}/prescriptions")
    public ResponseEntity<List<PrescriptionDTO>> getPatientPrescriptions(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientEMRService.getPrescriptionsByPatient(patientId));
    }
}
