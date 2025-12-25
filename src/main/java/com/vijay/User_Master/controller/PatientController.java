package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.lab.PatientDTO;
import com.vijay.User_Master.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lab/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@RequestBody PatientDTO patientDTO) {
        PatientDTO createdPatient = patientService.createPatient(patientDTO);
        return new ResponseEntity<>(createdPatient, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable Long id) {
        PatientDTO patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    @GetMapping
    public ResponseEntity<Page<PatientDTO>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PatientDTO> patients = patientService.getAllPatients(page, size);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPatients(
            @RequestParam(required = false) String uhid,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String mobile) {
        
        // Search by UHID takes priority
        if (uhid != null && !uhid.trim().isEmpty()) {
            PatientDTO patient = patientService.findByUhid(uhid.trim());
            return ResponseEntity.ok(patient);
        }
        
        // TODO: Add search by name and mobile if needed
        return ResponseEntity.badRequest().body("Please provide search criteria");
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(@PathVariable Long id, @RequestBody PatientDTO patientDTO) {
        PatientDTO updatedPatient = patientService.updatePatient(id, patientDTO);
        return ResponseEntity.ok(updatedPatient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
