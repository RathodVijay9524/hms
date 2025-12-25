package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.DoctorSchedule;
import lombok.*;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorScheduleResponse {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private DoctorSchedule.DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer slotDuration;
    private Boolean active;
}
