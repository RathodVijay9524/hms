package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.VitalSign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {
    Page<VitalSign> findByPatientIdAndOwnerId(Long patientId, Long ownerId, Pageable pageable);
    List<VitalSign> findByPatientIdAndOwnerIdOrderByRecordedAtDesc(Long patientId, Long ownerId);
    Optional<VitalSign> findByIdAndOwnerId(Long id, Long ownerId);
}
