-- Insert a CONSULTATION charge item for testing automated billing
-- This charge will be used when closing patient visits to auto-generate consultation bills

INSERT INTO charge_items (name, category, base_amount, tax_percent, active, owner_id, created_by, created_on, updated_by, updated_on)
VALUES 
    ('Doctor Consultation', 'CONSULTATION', 500.00, 0.00, true, 1, 'System', NOW(), 'System', NOW()),
    ('Specialist Consultation', 'CONSULTATION', 1000.00, 0.00, true, 1, 'System', NOW(), 'System', NOW()),
    ('Follow-up Consultation', 'CONSULTATION', 300.00, 0.00, true, 1, 'System', NOW(), 'System', NOW());

-- Verify the insert
SELECT * FROM charge_items WHERE category = 'CONSULTATION';
