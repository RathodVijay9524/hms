package com.vijay.User_Master.dto.reception;

import lombok.Data;
import java.time.LocalDate;
import com.vijay.User_Master.entity.QueueToken.TokenStatus;

@Data
public class QueueTokenDTO {
    private Long id;
    private String tokenNumber;
    private Long patientId;
    private String patientName;
    private String phone;
    private Long departmentId;
    private String departmentName;
    private Long doctorId;
    private String doctorName;
    private LocalDate tokenDate;
    private TokenStatus status;
}
