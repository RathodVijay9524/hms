package com.vijay.User_Master.dto.nursing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientAdmissionStatusDTO {
    private boolean isAdmitted;
    private Long wardId;
    private String wardName;
    private String bedCode;
    private String admissionDate;
    private String message;
    private String debugInfo;
}
