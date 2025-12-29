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
public class MedicationDueDTO {
    private Long administrationId;
    private LocalDateTime scheduledAt;

    private Long assignmentId;
    private String patientName;
    private String bedCode;

    private String medicineName;
    private String dosage;
    private String route;
}
