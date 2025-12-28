package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.lab.PatientDTO;
import com.vijay.User_Master.dto.reception.*;
import com.vijay.User_Master.entity.Appointment;
import java.util.List;

public interface ReceptionService {
    // Stats
    ReceptionStatsDTO getDashboardStats();
    ReceptionStatsDTO getStatsByDate(java.time.LocalDate date);

    // Enquiries
    EnquiryDTO createEnquiry(EnquiryDTO enquiryDTO);
    List<EnquiryDTO> getAllEnquiries();
    EnquiryDTO updateEnquiryStatus(Long id, String status, String notes);

    // Visitors
    VisitorLogDTO checkInVisitor(VisitorLogDTO visitorDTO);
    VisitorLogDTO checkOutVisitor(Long id);
    List<VisitorLogDTO> getAllVisitorLogs();

    // Tokens
    QueueTokenDTO issueToken(QueueTokenDTO tokenDTO);
    List<QueueTokenDTO> getTodayTokens();
    QueueTokenDTO updateTokenStatus(Long id, String status);
    
    // Quick Search for Patient
    Object searchPatients(String query);
    List<PatientDTO> getAllPatients();

    // Appointments
    List<com.vijay.User_Master.dto.AppointmentResponse> getTodayAppointments();
    List<com.vijay.User_Master.dto.AppointmentResponse> getAppointmentsByDate(java.time.LocalDate date);
}
