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
public class MedicationAdministrationDTO {
    private Long id;
    private Long wardId;
    private Long assignmentId;

    private String medicineName;
    private String dosage;
    private String route;
    private String instructions;

    private LocalDateTime scheduledAt;
    private String status;
    private LocalDateTime administeredAt;

    private String notes;
}
