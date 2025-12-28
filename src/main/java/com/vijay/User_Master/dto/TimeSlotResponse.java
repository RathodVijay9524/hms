package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.TimeSlot;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlotResponse {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private TimeSlot.SlotStatus status;
    private boolean available;
}
