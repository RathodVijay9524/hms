package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Optional<Prescription> findByVisitIdAndOwnerId(Long visitId, Long ownerId);
    Optional<Prescription> findByIdAndOwnerId(Long id, Long ownerId);
}
