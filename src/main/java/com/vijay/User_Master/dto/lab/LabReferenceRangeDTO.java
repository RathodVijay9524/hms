package com.vijay.User_Master.dto.lab;

import com.vijay.User_Master.entity.LabReferenceRange.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabReferenceRangeDTO {
    private Long id;
    private Gender gender;
    private Integer minAgeYears;
    private Integer maxAgeYears;
    private Double lowerLimit;
    private Double upperLimit;
    private Double criticalLow;
    private Double criticalHigh;
}
