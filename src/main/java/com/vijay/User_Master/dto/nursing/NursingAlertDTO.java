package com.vijay.User_Master.dto.nursing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NursingAlertDTO {
    private Long id;
    private Long wardId;
    private Long assignmentId;

    private String alertType;
    private String severity;
    private String message;

    private Boolean isAcknowledged;
    private LocalDateTime acknowledgedAt;

    private LocalDateTime createdDate;
}
