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
    
    // UHID queries
    Optional<Patient> findByUhidAndOwnerId(String uhid, Long ownerId);
    
    @org.springframework.data.jpa.repository.Query("SELECT p.uhid FROM Patient p WHERE p.uhid LIKE :pattern AND p.owner.id = :ownerId ORDER BY p.uhid DESC LIMIT 1")
    String findMaxUhidByPattern(@org.springframework.data.repository.query.Param("pattern") String pattern, 
                                @org.springframework.data.repository.query.Param("ownerId") Long ownerId);
    
    java.util.List<Patient> findByUhidIsNull();
    
    long countByOwnerId(Long ownerId);
}
