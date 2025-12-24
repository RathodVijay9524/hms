package com.vijay.User_Master.entity;

public enum LabCategory {
    BIOCHEMISTRY,
    HEMATOLOGY,
    MICROBIOLOGY,
    URINALYSIS,
    IMMUNOLOGY,
    PATHOLOGY,
    RADIOLOGY;

    @com.fasterxml.jackson.annotation.JsonCreator
    public static LabCategory fromString(String value) {
        if (value == null) return null;
        return LabCategory.valueOf(value.toUpperCase());
    }
}
