package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.DoctorVisit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorVisitRepository extends JpaRepository<DoctorVisit, Long> {
    Page<DoctorVisit> findByPatientIdAndOwnerId(Long patientId, Long ownerId, Pageable pageable);
    List<DoctorVisit> findByPatientIdAndOwnerIdOrderByVisitDateDesc(Long patientId, Long ownerId);
    Optional<DoctorVisit> findByIdAndOwnerId(Long id, Long ownerId);
    Page<DoctorVisit> findByDoctorIdAndOwnerId(Long doctorId, Long ownerId, Pageable pageable);
    
    @org.springframework.data.jpa.repository.Query("SELECT MAX(v.visitNumber) FROM DoctorVisit v WHERE v.visitNumber LIKE :pattern AND v.owner.id = :ownerId")
    String findMaxVisitNumberByPattern(String pattern, Long ownerId);
}
