package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.DoctorScheduleRequest;
import com.vijay.User_Master.dto.DoctorScheduleResponse;
import com.vijay.User_Master.dto.TimeSlotResponse;

import java.time.LocalDate;
import java.util.List;

public interface SchedulingService {
    DoctorScheduleResponse saveSchedule(DoctorScheduleRequest request);
    List<DoctorScheduleResponse> getDoctorSchedules(Long doctorId);
    List<TimeSlotResponse> getAvailableSlots(Long doctorId, LocalDate date);
    void generateSlotsForDate(Long doctorId, LocalDate date);
    void deleteSchedule(Long scheduleId);
}
