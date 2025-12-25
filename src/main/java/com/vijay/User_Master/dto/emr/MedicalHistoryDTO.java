package com.vijay.User_Master.dto.emr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistoryDTO {
    private Long id;
    private Long patientId;
    private String allergies;
    private String chronicConditions;
    private String pastSurgeries;
    private String familyHistory;
    private String bloodGroup;
}
