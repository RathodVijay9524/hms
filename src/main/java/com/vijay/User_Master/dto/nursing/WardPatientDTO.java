package com.vijay.User_Master.dto.nursing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WardPatientDTO {
    private Long assignmentId;
    private Long patientId;
    private String patientName;
    private String uhid;
    private String bedCode;

    private String gender;
    private Integer age;

    private String primaryDoctorName;
    private String lastAction;
    private String stability;
    private String bpSummary;
}
