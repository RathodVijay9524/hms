-- Migration script for Emergency Contact fields
-- Adds emergency contact information to patients table

ALTER TABLE patients 
ADD COLUMN emergency_contact_name VARCHAR(100),
ADD COLUMN emergency_contact_relationship VARCHAR(50),
ADD COLUMN emergency_contact_phone VARCHAR(20),
ADD COLUMN emergency_contact_alternate_phone VARCHAR(20);
