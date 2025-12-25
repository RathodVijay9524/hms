-- Migration script for BMI fields in vital_signs table

ALTER TABLE vital_signs 
ADD COLUMN IF NOT EXISTS height DOUBLE,
ADD COLUMN bmi DOUBLE,
ADD COLUMN bmi_category VARCHAR(20);
