package com.vijay.User_Master.dto.reception;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VisitorLogDTO {
    private Long id;
    private String visitorName;
    private String phone;
    private String idProofType;
    private String idProofNumber;
    private String purpose;
    private String whomToMeet;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String remarks;
}
