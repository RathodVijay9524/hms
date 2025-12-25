package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.AppointmentRequest;
import com.vijay.User_Master.dto.AppointmentResponse;
import com.vijay.User_Master.entity.Appointment;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {
    AppointmentResponse bookAppointment(AppointmentRequest request);
    AppointmentResponse updateStatus(Long id, Appointment.AppointmentStatus status);
    List<AppointmentResponse> getTodayAppointments();
    List<AppointmentResponse> getAppointmentsByPatient(Long patientId);
    AppointmentResponse getAppointmentById(Long id);
}
