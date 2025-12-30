package com.vijay.User_Master.dto.reception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorWeeklyScheduleDTO {
    private Long doctorId;
    private String doctorName;
    private String department;
    // Key: YYYY-MM-DD, Value: List of formatted shift strings e.g. "09 AM - 02 PM"
    private Map<String, List<String>> weeklyShifts;
    // To help frontend easy lookup by DayOfWeek name if needed, but date key is better for specific dates
}
