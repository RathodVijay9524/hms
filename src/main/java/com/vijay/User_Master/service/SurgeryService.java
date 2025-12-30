package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.SurgeryDTO;
import com.vijay.User_Master.entity.Surgery;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SurgeryService {
    List<SurgeryDTO> getSurgeriesForDoctor(Long doctorId, LocalDate date);
    List<SurgeryDTO> getAllSurgeries(LocalDate date);
    Map<String, Object> getDashboardStats(LocalDate date);
    SurgeryDTO scheduleSurgery(com.vijay.User_Master.dto.doctor.CreateSurgeryRequest request);
    List<SurgeryDTO> getSurgeriesByPatient(Long patientId);
}
