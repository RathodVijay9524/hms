package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.emr.PatientSymptomDTO;
import com.vijay.User_Master.entity.SymptomMaster;

import java.util.List;

/**
 * Service interface for patient symptom management
 */
public interface PatientSymptomService {
    
    /**
     * Add a symptom to a visit
     */
    PatientSymptomDTO addSymptom(Long visitId, PatientSymptomDTO symptomDTO);
    
    /**
     * Get all symptoms for a visit
     */
    List<PatientSymptomDTO> getSymptomsByVisit(Long visitId);
    
    /**
     * Get symptom history for a patient
     */
    List<PatientSymptomDTO> getSymptomsByPatient(Long patientId);
    
    /**
     * Delete a symptom
     */
    void deleteSymptom(Long symptomId);
    
    /**
     * Get all common symptoms for autocomplete
     */
    List<SymptomMaster> getAllCommonSymptoms();
    
    /**
     * Search symptoms by name
     */
    List<SymptomMaster> searchSymptoms(String query);
}
