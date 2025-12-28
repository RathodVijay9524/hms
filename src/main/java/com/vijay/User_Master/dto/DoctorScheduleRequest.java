package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.DoctorSchedule;
import lombok.*;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorScheduleRequest {
    private Long doctorId;
    private DoctorSchedule.DayOfWeek dayOfWeek;
    
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate specificDate;
    
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "HH:mm")
    private java.time.LocalTime startTime;
    
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "HH:mm")
    private java.time.LocalTime endTime;
    
    private Integer slotDuration;
    private Boolean active;
}
