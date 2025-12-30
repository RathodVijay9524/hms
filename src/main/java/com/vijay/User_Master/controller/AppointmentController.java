package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.dto.AppointmentRequest;
import com.vijay.User_Master.dto.AppointmentResponse;
import com.vijay.User_Master.entity.Appointment;
import com.vijay.User_Master.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Log4j2
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<?> bookAppointment(@RequestBody AppointmentRequest request) {
        log.info("Request to book appointment for patient: {}", request.getPatientId());
        AppointmentResponse response = appointmentService.bookAppointment(request);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam Appointment.AppointmentStatus status) {
        log.info("Request to update appointment {} status to {}", id, status);
        AppointmentResponse response = appointmentService.updateStatus(id, status);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayAppointments() {
        log.info("Request to get today's appointments");
        List<AppointmentResponse> response = appointmentService.getTodayAppointments();
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getAppointmentsByPatient(@PathVariable Long patientId) {
        log.info("Request to get appointments for patient: {}", patientId);
        List<AppointmentResponse> response = appointmentService.getAppointmentsByPatient(patientId);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointment(@PathVariable Long id) {
        log.info("Request to get appointment: {}", id);
        AppointmentResponse response = appointmentService.getAppointmentById(id);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }

    @GetMapping("/my-schedule")
    public ResponseEntity<?> getMyAppointments(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date) {
        if (date == null) date = java.time.LocalDate.now();
        log.info("Request to get my appointments for date: {}", date);
        List<AppointmentResponse> response = appointmentService.getMyAppointments(date);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }
    @GetMapping("/teleconsult")
    public ResponseEntity<?> getTeleconsultAppointments(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date) {
        if (date == null) date = java.time.LocalDate.now();
        log.info("Request to get teleconsult appointments for date: {}", date);
        List<AppointmentResponse> response = appointmentService.getTeleconsultAppointments(date);
        return ExceptionUtil.createBuildResponse(response, HttpStatus.OK);
    }
}
