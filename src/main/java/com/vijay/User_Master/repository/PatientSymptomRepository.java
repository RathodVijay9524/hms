package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.PatientSymptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for patient symptoms
 */
@Repository
public interface PatientSymptomRepository extends JpaRepository<PatientSymptom, Long> {
    
    /**
     * Find all symptoms for a specific visit
     */
    List<PatientSymptom> findByVisitIdAndOwnerIdOrderByCreatedOnDesc(Long visitId, Long ownerId);
    
    /**
     * Find all symptoms for a patient across all visits
     */
    List<PatientSymptom> findByVisitPatientIdAndOwnerIdOrderByCreatedOnDesc(Long patientId, Long ownerId);
}
