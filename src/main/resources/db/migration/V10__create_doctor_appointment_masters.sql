-- Migration script for Doctor & Appointment Masters

CREATE TABLE IF NOT EXISTS departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    owner_id BIGINT NOT NULL,
    created_by INT,
    updated_by INT,
    created_on DATETIME,
    updated_on DATETIME,
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS doctor_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    department_id BIGINT NOT NULL,
    specialization VARCHAR(255),
    qualification VARCHAR(255),
    registration_number VARCHAR(255),
    status BOOLEAN DEFAULT TRUE,
    owner_id BIGINT NOT NULL,
    created_by INT,
    updated_by INT,
    created_on DATETIME,
    updated_on DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (department_id) REFERENCES departments(id),
    FOREIGN KEY (owner_id) REFERENCES users(id)
);
