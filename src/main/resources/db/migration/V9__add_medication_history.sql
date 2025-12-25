-- Migration script for medication_history field in medical_history table

ALTER TABLE medical_history
ADD COLUMN medication_history TEXT;
