package com.vijay.User_Master.entity;

/**
 * Enum representing the severity level of a patient symptom
 */
public enum Severity {
    MILD,      // Minor discomfort, minimal impact on daily activities
    MODERATE,  // Noticeable symptoms, some impact on daily activities
    SEVERE,    // Significant symptoms, major impact on daily activities
    CRITICAL;  // Emergency level, requires immediate attention
    
    @com.fasterxml.jackson.annotation.JsonCreator
    public static Severity fromString(String value) {
        if (value == null) return null;
        return Severity.valueOf(value.toUpperCase());
    }
}
