package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.emr.EMRAuditLogDTO;
import com.vijay.User_Master.entity.EMRAuditLog;
import com.vijay.User_Master.entity.EMRAuditLog.EntityType;
import com.vijay.User_Master.repository.EMRAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for EMR audit logs
 */
@RestController
@RequestMapping("/api/emr/audit")
@RequiredArgsConstructor
public class EMRAuditController {
    
    private final EMRAuditLogRepository auditLogRepository;
    private final ModelMapper modelMapper;
    
    /**
     * Get audit logs for a specific entity
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<Page<EMRAuditLogDTO>> getEntityAuditLog(
            @PathVariable EntityType entityType,
            @PathVariable Long entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        Pageable pageable = PageRequest.of(page, size);
        
        Page<EMRAuditLog> logs = auditLogRepository
            .findByEntityTypeAndEntityIdAndOwnerIdOrderByChangedAtDesc(
                entityType, entityId, ownerId, pageable);
        
        return ResponseEntity.ok(logs.map(log -> modelMapper.map(log, EMRAuditLogDTO.class)));
    }
    
    /**
     * Get all audit logs with optional filtering
     */
    @GetMapping
    public ResponseEntity<Page<EMRAuditLogDTO>> getAllAuditLogs(
            @RequestParam(required = false) EntityType entityType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        Pageable pageable = PageRequest.of(page, size);
        
        Page<EMRAuditLog> logs;
        if (entityType != null) {
            logs = auditLogRepository.findByEntityTypeAndOwnerIdOrderByChangedAtDesc(
                entityType, ownerId, pageable);
        } else {
            logs = auditLogRepository.findByOwnerIdOrderByChangedAtDesc(ownerId, pageable);
        }
        
        return ResponseEntity.ok(logs.map(log -> modelMapper.map(log, EMRAuditLogDTO.class)));
    }
}
