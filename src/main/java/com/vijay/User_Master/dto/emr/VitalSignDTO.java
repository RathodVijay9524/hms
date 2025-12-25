package com.vijay.User_Master.dto.emr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VitalSignDTO {
    private Long id;
    private Long patientId;
    private LocalDateTime recordedAt;
    private Integer systolicBP;
    private Integer diastolicBP;
    private Integer heartRate;
    private Double temperature;
    private Double weight;
    private Double height;
    private Double bmi;
    private String bmiCategory;
    private Integer oxygenLevel;
    
    // Helper for formatted display
    public String getBp() {
        if (systolicBP != null && diastolicBP != null) {
            return systolicBP + "/" + diastolicBP;
        }
        return "-";
    }
}
