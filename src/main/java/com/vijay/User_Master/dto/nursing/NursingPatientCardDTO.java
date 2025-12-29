package com.vijay.User_Master.dto.nursing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NursingPatientCardDTO {
    private Long assignmentId;
    private Long patientId;
    private String patientName;
    private String uhid;
    private String bedCode;

    private String stability;
    private String bpSummary;
}
