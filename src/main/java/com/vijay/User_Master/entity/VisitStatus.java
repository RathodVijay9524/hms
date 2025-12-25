package com.vijay.User_Master.entity;

/**
 * Represents the lifecycle status of a doctor visit/encounter.
 * Ensures medical-legal compliance through state management.
 */
public enum VisitStatus {
    /**
     * Initial state when visit is created.
     * Visit can be edited freely.
     */
    CREATED,
    
    /**
     * Doctor has started examination.
     * Visit can still be edited.
     */
    IN_PROGRESS,
    
    /**
     * Visit completed and closed.
     * Becomes read-only for medical-legal compliance.
     */
    CLOSED,
    
    /**
     * Permanently locked after closure period.
     * Immutable for audit purposes.
     */
    LOCKED
}
