package com.vijay.User_Master.dto.emr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileDTO {
    private Long id;
    private Long userId;
    private String doctorName;
    private Long departmentId;
    private String departmentName;
    private String specialization;
    private String qualification;
    private String registrationNumber;
    private Boolean status;
}
