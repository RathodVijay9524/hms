package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.lab.PatientDTO;
import org.springframework.data.domain.Page;

public interface PatientService {
    PatientDTO createPatient(PatientDTO patientDTO);
    PatientDTO getPatientById(Long id);
    Page<PatientDTO> getAllPatients(int page, int size);
    PatientDTO updatePatient(Long id, PatientDTO patientDTO);
    void deletePatient(Long id);
    long getPatientCount();
}
