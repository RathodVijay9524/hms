package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
    Optional<MedicalHistory> findByPatientIdAndOwnerId(Long patientId, Long ownerId);
    Optional<MedicalHistory> findByIdAndOwnerId(Long id, Long ownerId);
}
