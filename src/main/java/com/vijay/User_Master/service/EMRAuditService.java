package com.vijay.User_Master.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.entity.EMRAuditLog;
import com.vijay.User_Master.entity.EMRAuditLog.AuditAction;
import com.vijay.User_Master.entity.EMRAuditLog.EntityType;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.EMRAuditLogRepository;
import com.vijay.User_Master.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Service for managing EMR audit logs.
 * Tracks all changes to EMR data for compliance.
 */
@Service
@RequiredArgsConstructor
public class EMRAuditService {
    
    private final EMRAuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    /**
     * Log a change to an EMR entity
     */
    @Transactional
    public void logChange(EntityType entityType, Long entityId, 
                         AuditAction action, Object before, Object after) {
        try {
            String username = CommonUtils.getLoggedInUser().getName();
            Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
            User owner = userRepository.findById(ownerId).orElseThrow();
            
            EMRAuditLog log = EMRAuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .changedBy(username)
                .changedAt(LocalDateTime.now())
                .beforeSnapshot(toJson(before))
                .afterSnapshot(toJson(after))
                .changeDiff(generateDiff(entityType, before, after, action))
                .owner(owner)
                .build();
                
            auditLogRepository.save(log);
        } catch (Exception e) {
            // Log error but don't fail the main operation
            System.err.println("Failed to create audit log: " + e.getMessage());
        }
    }
    
    /**
     * Convert object to JSON string
     */
    private String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "Error serializing: " + e.getMessage();
        }
    }
    
    /**
     * Generate human-readable diff
     */
    private String generateDiff(EntityType entityType, Object before, Object after, AuditAction action) {
        if (action == AuditAction.CREATE) {
            return "Created new " + entityType.name().toLowerCase().replace('_', ' ');
        }
        
        if (action == AuditAction.DELETE) {
            return "Deleted " + entityType.name().toLowerCase().replace('_', ' ');
        }
        
        // For UPDATE, try to generate a meaningful diff
        if (before == null || after == null) {
            return "Updated " + entityType.name().toLowerCase().replace('_', ' ');
        }
        
        try {
            Map<String, Object> beforeMap = objectMapper.convertValue(before, Map.class);
            Map<String, Object> afterMap = objectMapper.convertValue(after, Map.class);
            
            StringBuilder diff = new StringBuilder();
            afterMap.forEach((key, newValue) -> {
                Object oldValue = beforeMap.get(key);
                if (oldValue != null && !oldValue.equals(newValue)) {
                    diff.append(String.format("%s changed from '%s' to '%s'; ", 
                        formatFieldName(key), oldValue, newValue));
                }
            });
            
            return diff.length() > 0 ? diff.toString() : "Updated " + entityType.name().toLowerCase().replace('_', ' ');
        } catch (Exception e) {
            return "Updated " + entityType.name().toLowerCase().replace('_', ' ');
        }
    }
    
    /**
     * Format field name for display
     */
    private String formatFieldName(String fieldName) {
        // Convert camelCase to Title Case
        return fieldName.replaceAll("([A-Z])", " $1")
                       .substring(0, 1).toUpperCase() + 
               fieldName.replaceAll("([A-Z])", " $1").substring(1);
    }
}
