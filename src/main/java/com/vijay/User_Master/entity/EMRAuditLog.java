package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity for tracking all changes to EMR data.
 * Provides complete audit trail for medical-legal compliance.
 */
@Entity
@Table(name = "emr_audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EMRAuditLog extends BaseModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EntityType entityType;
    
    @Column(nullable = false)
    private Long entityId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuditAction action;
    
    @Column(nullable = false, length = 100)
    private String changedBy;
    
    @Column(nullable = false)
    private LocalDateTime changedAt;
    
    @Column(columnDefinition = "TEXT")
    private String beforeSnapshot;
    
    @Column(columnDefinition = "TEXT")
    private String afterSnapshot;
    
    @Column(columnDefinition = "TEXT")
    private String changeDiff;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    /**
     * Types of entities that can be audited
     */
    public enum EntityType {
        MEDICAL_HISTORY,
        VITAL_SIGN,
        DOCTOR_VISIT,
        PRESCRIPTION,
        PATIENT
    }
    
    /**
     * Types of actions that can be performed
     */
    public enum AuditAction {
        CREATE,
        UPDATE,
        DELETE
    }
}
