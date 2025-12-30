package com.vijay.User_Master.dto.doctor;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateSurgeryRequest {
    private String uhid;
    private String procedureName;
    private LocalDateTime scheduledStartTime;
    private Integer durationMinutes; // Default 60
    private String otCode;
    private String departmentName;
    private String anaesthetistName; // Optional
}
