package com.vijay.User_Master.dto.nursing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NursingHandoverDTO {
    private Long id;
    private Long wardId;

    private LocalDate handoverDate;
    private String fromShift;
    private String toShift;

    private String reportText;
    private String checklistJson;

    private Long signedOffBy;
    private LocalDateTime signedOffAt;
}
