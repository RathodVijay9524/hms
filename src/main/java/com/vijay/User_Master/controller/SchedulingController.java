package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.dto.DoctorScheduleRequest;
import com.vijay.User_Master.dto.DoctorScheduleResponse;
import com.vijay.User_Master.dto.TimeSlotResponse;
import com.vijay.User_Master.service.SchedulingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/scheduling")
@RequiredArgsConstructor
@Log4j2
public class SchedulingController {

    private final SchedulingService schedulingService;

    @PostMapping("/doctor-schedule")
    public ResponseEntity<?> saveSchedule(@RequestBody DoctorScheduleRequest request) {
        log.info("Request to save schedule for doctor: {}", request.getDoctorId());
        DoctorScheduleResponse response = schedulingService.saveSchedule(request);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.CREATED);
    }

    @GetMapping("/doctor-schedule/{doctorId}")
    public ResponseEntity<?> getDoctorSchedules(@PathVariable Long doctorId) {
        log.info("Request to get schedules for doctor: {}", doctorId);
        List<DoctorScheduleResponse> response = schedulingService.getDoctorSchedules(doctorId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/slots")
    public ResponseEntity<?> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Request to get slots for doctor: {} on {}", doctorId, date);
        List<TimeSlotResponse> response = schedulingService.getAvailableSlots(doctorId, date);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @DeleteMapping("/doctor-schedule/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long scheduleId) {
        log.info("Request to delete schedule: {}", scheduleId);
        schedulingService.deleteSchedule(scheduleId);
        return ExceptionUtil.createBuildResponse("Schedule deleted successfully", HttpStatus.OK);
    }
}
