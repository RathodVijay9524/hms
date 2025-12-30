package com.vijay.User_Master.dto.doctor;

import lombok.Data;

@Data
public class AdmitPatientRequest {
    private String uhid;
    private Long wardId;
    private String bedCode;
}
