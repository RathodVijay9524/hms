package com.vijay.User_Master.dto.emr;

import com.vijay.User_Master.entity.EMRAuditLog.AuditAction;
import com.vijay.User_Master.entity.EMRAuditLog.EntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for EMR audit log entries
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EMRAuditLogDTO {
    private Long id;
    private EntityType entityType;
    private Long entityId;
    private AuditAction action;
    private String changedBy;
    private LocalDateTime changedAt;
    private String beforeSnapshot;
    private String afterSnapshot;
    private String changeDiff;
}
