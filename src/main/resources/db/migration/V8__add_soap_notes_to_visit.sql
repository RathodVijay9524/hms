-- Migration script for SOAP fields in doctor_visits table

ALTER TABLE doctor_visits
ADD COLUMN subjective_notes TEXT,
ADD COLUMN objective_notes TEXT,
ADD COLUMN assessment_notes TEXT,
ADD COLUMN plan_notes TEXT;
