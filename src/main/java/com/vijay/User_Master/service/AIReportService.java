package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.lab.LabOrderDTO;

public interface AIReportService {
    /**
     * Generates a clinical insight summary for a given lab order using AI.
     * 
     * @param orderId The ID of the lab order.
     * @return The updated LabOrderDTO with AI summary.
     */
    LabOrderDTO generateAIReportSummary(Long orderId);
}
