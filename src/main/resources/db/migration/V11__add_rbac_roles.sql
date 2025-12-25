-- Migration script to add standard RBAC roles
-- This ensures all roles are available for assignment to users

INSERT IGNORE INTO roles (name, is_active, is_deleted, created_on, updated_on) VALUES
('ROLE_ADMIN', true, false, NOW(), NOW()),
('ROLE_SUPER_ADMIN', true, false, NOW(), NOW()),
('ROLE_IT_SUPPORT', true, false, NOW(), NOW()),
('ROLE_DOCTOR', true, false, NOW(), NOW()),
('ROLE_NURSE', true, false, NOW(), NOW()),
('ROLE_SURGEON', true, false, NOW(), NOW()),
('ROLE_ANESTHESIOLOGIST', true, false, NOW(), NOW()),
('ROLE_PHYSIOTHERAPIST', true, false, NOW(), NOW()),
('ROLE_PATIENT', true, false, NOW(), NOW()),
('ROLE_RECEPTIONIST', true, false, NOW(), NOW()),
('ROLE_FRONT_DESK', true, false, NOW(), NOW()),
('ROLE_LAB_TECHNICIAN', true, false, NOW(), NOW()),
('ROLE_RADIOLOGIST', true, false, NOW(), NOW()),
('ROLE_PHARMACIST', true, false, NOW(), NOW()),
('ROLE_BILLING', true, false, NOW(), NOW()),
('ROLE_ACCOUNTANT', true, false, NOW(), NOW()),
('ROLE_INSURANCE_COORDINATOR', true, false, NOW(), NOW()),
('ROLE_HOSPITAL_MANAGER', true, false, NOW(), NOW()),
('ROLE_DEPARTMENT_HEAD', true, false, NOW(), NOW()),
('ROLE_HR', true, false, NOW(), NOW()),
('ROLE_INVENTORY_MANAGER', true, false, NOW(), NOW()),
('ROLE_PROCUREMENT', true, false, NOW(), NOW()),
('ROLE_MEDICAL_RECORDS', true, false, NOW(), NOW()),
('ROLE_COMPLIANCE_OFFICER', true, false, NOW(), NOW()),
('ROLE_SECURITY', true, false, NOW(), NOW());
