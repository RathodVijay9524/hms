package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.EMRAuditLog;
import com.vijay.User_Master.entity.EMRAuditLog.EntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for EMR audit logs
 */
@Repository
public interface EMRAuditLogRepository extends JpaRepository<EMRAuditLog, Long> {
    
    /**
     * Find audit logs for a specific entity
     */
    Page<EMRAuditLog> findByEntityTypeAndEntityIdAndOwnerIdOrderByChangedAtDesc(
        EntityType entityType, Long entityId, Long ownerId, Pageable pageable);
    
    /**
     * Find all audit logs for an owner
     */
    Page<EMRAuditLog> findByOwnerIdOrderByChangedAtDesc(
        Long ownerId, Pageable pageable);
    
    /**
     * Find audit logs by entity type
     */
    Page<EMRAuditLog> findByEntityTypeAndOwnerIdOrderByChangedAtDesc(
        EntityType entityType, Long ownerId, Pageable pageable);
}
