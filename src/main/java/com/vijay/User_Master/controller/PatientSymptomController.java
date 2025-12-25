package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.emr.PatientSymptomDTO;
import com.vijay.User_Master.entity.SymptomMaster;
import com.vijay.User_Master.service.PatientSymptomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for patient symptom management
 */
@RestController
@RequestMapping("/api/emr")
@RequiredArgsConstructor
public class PatientSymptomController {
    
    private final PatientSymptomService symptomService;
    
    /**
     * Add a symptom to a visit
     */
    @PostMapping("/visits/{visitId}/symptoms")
    public ResponseEntity<PatientSymptomDTO> addSymptom(
            @PathVariable Long visitId,
            @RequestBody PatientSymptomDTO symptomDTO) {
        return ResponseEntity.ok(symptomService.addSymptom(visitId, symptomDTO));
    }
    
    /**
     * Get all symptoms for a visit
     */
    @GetMapping("/visits/{visitId}/symptoms")
    public ResponseEntity<List<PatientSymptomDTO>> getVisitSymptoms(@PathVariable Long visitId) {
        return ResponseEntity.ok(symptomService.getSymptomsByVisit(visitId));
    }
    
    /**
     * Get symptom history for a patient
     */
    @GetMapping("/patients/{patientId}/symptoms")
    public ResponseEntity<List<PatientSymptomDTO>> getPatientSymptoms(@PathVariable Long patientId) {
        return ResponseEntity.ok(symptomService.getSymptomsByPatient(patientId));
    }
    
    /**
     * Delete a symptom
     */
    @DeleteMapping("/symptoms/{symptomId}")
    public ResponseEntity<Void> deleteSymptom(@PathVariable Long symptomId) {
        symptomService.deleteSymptom(symptomId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get all common symptoms for autocomplete
     */
    @GetMapping("/symptoms/master")
    public ResponseEntity<List<SymptomMaster>> getAllSymptoms() {
        return ResponseEntity.ok(symptomService.getAllCommonSymptoms());
    }
    
    /**
     * Search symptoms by name
     */
    @GetMapping("/symptoms/search")
    public ResponseEntity<List<SymptomMaster>> searchSymptoms(@RequestParam String q) {
        return ResponseEntity.ok(symptomService.searchSymptoms(q));
    }
}
