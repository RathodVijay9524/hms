package com.vijay.User_Master.util;

import com.vijay.User_Master.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Utility class for generating Unique Hospital IDs (UHID) for patients.
 * Format: H{businessId}-{year}-{sequence}
 * Example: H1-2025-000001
 */
@Component
@RequiredArgsConstructor
public class UHIDGenerator {
    
    private final PatientRepository patientRepository;
    
    /**
     * Generate a new UHID for a patient
     * @param businessId The owner/business ID
     * @return UHID in format H{businessId}-{year}-{sequence}
     */
    public String generate(Long businessId) {
        String year = String.valueOf(LocalDate.now().getYear());
        int sequence = getNextSequence(businessId, year);
        return String.format("H%d-%s-%06d", businessId, year, sequence);
    }
    
    /**
     * Get the next sequence number for a business/year combination
     * @param businessId The owner/business ID
     * @param year The current year
     * @return Next sequence number
     */
    private int getNextSequence(Long businessId, String year) {
        // Find max UHID for this business + year
        String pattern = String.format("H%d-%s-%%", businessId, year);
        String maxUhid = patientRepository.findMaxUhidByPattern(pattern, businessId);
        
        if (maxUhid == null || maxUhid.isEmpty()) {
            return 1;
        }
        
        // Extract sequence from "H1-2025-000123" -> 123
        try {
            String sequencePart = maxUhid.substring(maxUhid.lastIndexOf('-') + 1);
            return Integer.parseInt(sequencePart) + 1;
        } catch (Exception e) {
            // If parsing fails, start from 1
            return 1;
        }
    }
}
