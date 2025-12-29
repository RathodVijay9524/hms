package com.vijay.User_Master.dto.nursing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchVitalEntryDTO {
    private Long patientId;
    private String bp; // "120/80"
    private Double temperature;
    private Integer pulse;
    private Integer oxygenLevel;
    private String notes;
}
