-- Migration script for Symptom Tracking
-- Creates patient_symptoms and symptom_master tables

CREATE TABLE patient_symptoms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    visit_id BIGINT NOT NULL,
    symptom_name VARCHAR(100) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    duration VARCHAR(50),
    notes TEXT,
    owner_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (visit_id) REFERENCES doctor_visits(id) ON DELETE CASCADE,
    FOREIGN KEY (owner_id) REFERENCES users(id),
    INDEX idx_visit (visit_id),
    INDEX idx_owner (owner_id)
);

CREATE TABLE symptom_master (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_name (name),
    INDEX idx_category (category)
);

-- Pre-populate common symptoms
INSERT INTO symptom_master (name, category) VALUES
('Fever', 'General'),
('Cough', 'Respiratory'),
('Headache', 'Neurological'),
('Nausea', 'Digestive'),
('Fatigue', 'General'),
('Chest Pain', 'Cardiovascular'),
('Shortness of Breath', 'Respiratory'),
('Abdominal Pain', 'Digestive'),
('Dizziness', 'Neurological'),
('Sore Throat', 'Respiratory'),
('Vomiting', 'Digestive'),
('Diarrhea', 'Digestive'),
('Back Pain', 'Musculoskeletal'),
('Joint Pain', 'Musculoskeletal'),
('Runny Nose', 'Respiratory'),
('Sneezing', 'Respiratory'),
('Loss of Appetite', 'General'),
('Weakness', 'General'),
('Chills', 'General'),
('Sweating', 'General');
