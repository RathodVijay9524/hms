package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurgeryDTO {
    private Long id;
    private String otCode;
    private LocalDateTime scheduledStartTime;
    private LocalDateTime scheduledEndTime;
    private String patientName;
    private String uhid;
    private String departmentName;
    private String procedureName;
    private String leadDoctorName;
    private String anaesthetistName;
    private String status;
}
