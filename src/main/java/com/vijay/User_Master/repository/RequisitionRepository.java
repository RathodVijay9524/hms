package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Requisition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RequisitionRepository extends JpaRepository<Requisition, Long> {
    
    java.util.Optional<Requisition> findByIdAndOwnerId(Long id, Long ownerId);

    List<Requisition> findByOwnerIdAndIsDeletedFalse(Long ownerId);
    
    Page<Requisition> findByOwnerIdAndIsDeletedFalse(Long ownerId, Pageable pageable);
    
    List<Requisition> findByRequestingDepartmentAndOwnerIdAndIsDeletedFalse(String department, Long ownerId);
    
    List<Requisition> findByStatusAndOwnerIdAndIsDeletedFalse(Requisition.RequisitionStatus status, Long ownerId);
    
    List<Requisition> findByPriorityAndOwnerIdAndIsDeletedFalse(Requisition.RequisitionPriority priority, Long ownerId);
    
    @Query("SELECT r FROM Requisition r WHERE r.owner.id = :ownerId AND r.isDeleted = false AND r.priority = 'CRITICAL' AND r.status = 'PENDING'")
    List<Requisition> findCriticalPendingRequisitions(@Param("ownerId") Long ownerId);
    
    @Query("SELECT r FROM Requisition r WHERE r.owner.id = :ownerId AND r.isDeleted = false AND r.status = 'PENDING'")
    List<Requisition> findPendingRequisitions(@Param("ownerId") Long ownerId);
    
    @Query("SELECT r FROM Requisition r WHERE r.owner.id = :ownerId AND r.isDeleted = false AND r.status = 'APPROVED'")
    List<Requisition> findProcessingRequisitions(@Param("ownerId") Long ownerId);
    
    @Query("SELECT COUNT(r) FROM Requisition r WHERE r.owner.id = :ownerId AND r.isDeleted = false AND r.status = 'PENDING'")
    Long getPendingRequisitionsCount(@Param("ownerId") Long ownerId);
    
    @Query("SELECT COUNT(r) FROM Requisition r WHERE r.owner.id = :ownerId AND r.isDeleted = false AND r.priority = 'CRITICAL' AND r.status = 'PENDING'")
    Long getCriticalRequisitionsCount(@Param("ownerId") Long ownerId);
}
