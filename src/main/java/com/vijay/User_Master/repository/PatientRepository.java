package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Page<Patient> findByOwnerId(Long ownerId, Pageable pageable);
    Optional<Patient> findByIdAndOwnerId(Long id, Long ownerId);
    Optional<Patient> findByPhoneAndOwnerId(String phone, Long ownerId);
}
