-- Migration script for UHID (Unique Hospital ID)
-- Adds UHID column to patients table

-- Add UHID column
ALTER TABLE patients 
ADD COLUMN uhid VARCHAR(20) UNIQUE;

-- Create index on UHID for faster searches
CREATE INDEX idx_patients_uhid ON patients(uhid);

-- Note: UHID generation for existing patients will be done via application code
-- to ensure proper sequence management per business/year combination
