-- Migration script for Visit Lifecycle Management
-- Adds status tracking fields to doctor_visits table

-- Add status column with default value
ALTER TABLE doctor_visits 
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'CREATED';

-- Add closure tracking fields
ALTER TABLE doctor_visits
ADD COLUMN closed_at TIMESTAMP NULL,
ADD COLUMN closed_by VARCHAR(100) NULL;

-- Add lock tracking fields
ALTER TABLE doctor_visits
ADD COLUMN locked_at TIMESTAMP NULL,
ADD COLUMN locked_by VARCHAR(100) NULL;

-- Add doctor_name for quick access (denormalized)
ALTER TABLE doctor_visits
ADD COLUMN doctor_name VARCHAR(255) NULL;

-- Create index on status for faster queries
CREATE INDEX idx_doctor_visits_status ON doctor_visits(status);

-- Update existing visits to CLOSED status (they are historical records)
UPDATE doctor_visits 
SET status = 'CLOSED', 
    closed_at = created_at,
    closed_by = 'SYSTEM_MIGRATION'
WHERE id IS NOT NULL;

-- Update doctor_name for existing records
UPDATE doctor_visits dv
INNER JOIN users u ON dv.doctor_id = u.id
SET dv.doctor_name = u.name
WHERE dv.doctor_name IS NULL;
