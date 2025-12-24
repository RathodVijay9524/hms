package com.vijay.User_Master.dto.lab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabResultDTO {
    private Long id;
    private Long orderId;
    private Long parameterId;
    private String parameterName;
    private String resultValue;
    private String technicianNotes;
    private String aiSummary;
    private boolean doctorVerified;
    private String doctorRemarks;
}
