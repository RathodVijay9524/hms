-- Migration script for EMR Audit Trail
-- Creates audit log table for tracking all EMR changes

CREATE TABLE emr_audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL,
    changed_by VARCHAR(100) NOT NULL,
    changed_at TIMESTAMP NOT NULL,
    before_snapshot TEXT,
    after_snapshot TEXT,
    change_diff TEXT,
    owner_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (owner_id) REFERENCES users(id),
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_owner_time (owner_id, changed_at DESC),
    INDEX idx_entity_owner (entity_type, entity_id, owner_id)
);
