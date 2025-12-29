package com.vijay.User_Master.dto.nursing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NurseScheduleDTO {
    private Long id;
    private Long nurseId;
    private String nurseName;
    private Long wardId;
    private String wardName;
    private LocalDate scheduleDate;
    private String shiftName;
    private LocalTime startTime;
    private LocalTime endTime;
    private String notes;
}
