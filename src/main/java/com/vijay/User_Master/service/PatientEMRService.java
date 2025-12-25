package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.emr.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PatientEMRService {
    
    // Medical History
    MedicalHistoryDTO getMedicalHistory(Long patientId);
    MedicalHistoryDTO saveMedicalHistory(Long patientId, MedicalHistoryDTO historyDTO);
    
    // Vital Signs
    VitalSignDTO addVitalSign(Long patientId, VitalSignDTO vitalSignDTO);
    Page<VitalSignDTO> getVitalSigns(Long patientId, int page, int size);
    List<VitalSignDTO> getRecentVitalSigns(Long patientId); // For charts
    
    // Doctor Visits
    DoctorVisitDTO createVisit(Long patientId, DoctorVisitDTO visitDTO);
    Page<DoctorVisitDTO> getPatientVisits(Long patientId, int page, int size);
    DoctorVisitDTO getVisitDetails(Long visitId);

    // Visit Lifecycle Management
    DoctorVisitDTO startVisit(Long visitId);
    DoctorVisitDTO closeVisit(Long visitId);
    DoctorVisitDTO lockVisit(Long visitId);
    
    // Prescriptions

    PrescriptionDTO addPrescription(Long visitId, PrescriptionDTO prescriptionDTO);
    PrescriptionDTO getPrescriptionByVisit(Long visitId);
    List<PrescriptionDTO> getPrescriptionsByPatient(Long patientId);
}
