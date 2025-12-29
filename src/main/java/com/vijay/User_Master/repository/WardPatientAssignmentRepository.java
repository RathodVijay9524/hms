package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.WardPatientAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WardPatientAssignmentRepository extends JpaRepository<WardPatientAssignment, Long> {

    Optional<WardPatientAssignment> findByIdAndOwnerId(Long id, Long ownerId);

    List<WardPatientAssignment> findByWardIdAndOwnerIdAndIsDeletedFalseAndStatus(
            Long wardId,
            Long ownerId,
            WardPatientAssignment.AssignmentStatus status
    );

    Page<WardPatientAssignment> findByWardIdAndOwnerIdAndIsDeletedFalseAndStatus(
            Long wardId,
            Long ownerId,
            WardPatientAssignment.AssignmentStatus status,
            Pageable pageable
    );

    List<WardPatientAssignment> findByPatientIdAndOwnerIdAndIsDeletedFalseAndStatus(
            Long patientId,
            Long ownerId,
            WardPatientAssignment.AssignmentStatus status
    );
}
