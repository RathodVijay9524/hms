package com.vijay.User_Master.service;

import java.util.Map;

public interface AIService {
    
    /**
     * Calculates the Modified Early Warning Score (MEWS) for a patient.
     * @param patientId ID of the patient.
     * @return A map containing the score and the risk category.
     */
    Map<String, Object> calculatePatientRiskScore(Long patientId);

    /**
     * Forecasts hospital admission volume for the next 24-48 hours.
     * @return A map with predicted counts and confidence intervals.
     */
    Map<String, Object> forecastAdmissionVolume();

    /**
     * Generates a draft discharge summary based on recent visit data.
     * @param visitId ID of the patient visit.
     * @return AI-generated text draft.
     */
    String generateDischargeDraft(Long visitId);

    /**
     * Predicts demand for a specific inventory category.
     * @param category Category name (e.g., "Surgical", "Pharma").
     * @return Predicted inventory requirement for the next month.
     */
    Map<String, Object> predictInventoryDemand(String category);

    /**
     * Audits a billing claim for potential insurance denials.
     * @param billId ID of the bill.
     * @return Risk percentage and identified anomaly reasons.
     */
    Map<String, Object> auditMedicalClaim(Long billId);
}
