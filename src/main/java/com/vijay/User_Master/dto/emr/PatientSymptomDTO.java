package com.vijay.User_Master.dto.emr;

import com.vijay.User_Master.entity.Severity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for patient symptom
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientSymptomDTO {
    private Long id;
    private Long visitId;
    private String symptomName;
    private Severity severity;
    private String duration;
    private String notes;
    private LocalDateTime createdAt;
}
