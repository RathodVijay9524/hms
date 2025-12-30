package com.vijay.User_Master.controller.doctor;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.SurgeryDTO;
import com.vijay.User_Master.service.SurgeryService;
import com.vijay.User_Master.Helper.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor/surgeries")
@RequiredArgsConstructor
@Slf4j
public class DoctorSurgeryController {

    private final SurgeryService surgeryService;

    @GetMapping("/schedule")
    public ResponseEntity<?> getSchedule(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        if (date == null) date = LocalDate.now();
        
        // Doctors see the full OT schedule
        List<SurgeryDTO> surgeries = surgeryService.getAllSurgeries(date);
        return ExceptionUtil.createBuildResponse(surgeries, HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        if (date == null) date = LocalDate.now();
        Map<String, Object> stats = surgeryService.getDashboardStats(date);
        return ExceptionUtil.createBuildResponse(stats, HttpStatus.OK);
    }
    @org.springframework.web.bind.annotation.PostMapping("/schedule")
    public ResponseEntity<?> scheduleSurgery(@org.springframework.web.bind.annotation.RequestBody com.vijay.User_Master.dto.doctor.CreateSurgeryRequest request) {
        return ExceptionUtil.createBuildResponse(surgeryService.scheduleSurgery(request), HttpStatus.CREATED);
    }
}
