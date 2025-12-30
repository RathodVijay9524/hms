package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.SurgeryDTO;
import com.vijay.User_Master.entity.Surgery;
import com.vijay.User_Master.repository.SurgeryRepository;
import com.vijay.User_Master.service.SurgeryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurgeryServiceImpl implements SurgeryService {

    private final SurgeryRepository surgeryRepository;
    private final com.vijay.User_Master.repository.DoctorProfileRepository doctorProfileRepository;
    private final com.vijay.User_Master.repository.PatientRepository patientRepository;
    private final com.vijay.User_Master.repository.UserRepository userRepository;

    private Long getOwnerId() {
        return CommonUtils.getLoggedInUser().getOwnerId();
    }

    @Override
    @Transactional
    public SurgeryDTO scheduleSurgery(com.vijay.User_Master.dto.doctor.CreateSurgeryRequest request) {
        Long ownerId = getOwnerId();
        Long userId = CommonUtils.getLoggedInUser().getId();

        com.vijay.User_Master.entity.DoctorProfile doctor = doctorProfileRepository.findByUserIdAndOwnerId(userId, ownerId)
                .orElseThrow(() -> new com.vijay.User_Master.exceptions.ResourceNotFoundException("Doctor", "ID", userId));

        com.vijay.User_Master.entity.Patient patient = patientRepository.findByUhidAndOwnerId(request.getUhid(), ownerId)
                .orElseThrow(() -> new com.vijay.User_Master.exceptions.ResourceNotFoundException("Patient", "UHID", request.getUhid()));

        com.vijay.User_Master.entity.User ownerEntity = userRepository.findById(ownerId)
                .orElseThrow(() -> new com.vijay.User_Master.exceptions.ResourceNotFoundException("Owner", "ID", ownerId));

        Surgery surgery = Surgery.builder()
                .patient(patient)
                .leadDoctor(doctor)
                .otCode(request.getOtCode())
                .procedureName(request.getProcedureName())
                .departmentName(request.getDepartmentName())
                .scheduledStartTime(request.getScheduledStartTime())
                .scheduledEndTime(request.getScheduledStartTime().plusMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 60))
                .status(Surgery.SurgeryStatus.SCHEDULED)
                .owner(ownerEntity)
                .anaesthetistName(request.getAnaesthetistName())
                .build();

        surgery = surgeryRepository.save(surgery);
        return toDTO(surgery);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurgeryDTO> getSurgeriesForDoctor(Long doctorId, LocalDate date) {
        Long ownerId = getOwnerId();
        LocalDateTime refreshStart = date.atStartOfDay();
        LocalDateTime refreshEnd = date.atTime(23, 59, 59);

        // If doctorId is provided, filter by it. If null, might want all (e.g. for OT manager View)
        // But the method name says ForDoctor.
        return surgeryRepository.findByLeadDoctorIdAndOwnerIdAndScheduledStartTimeBetween(doctorId, ownerId, refreshStart, refreshEnd)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurgeryDTO> getAllSurgeries(LocalDate date) {
         Long ownerId = getOwnerId();
        LocalDateTime refreshStart = date.atStartOfDay();
        LocalDateTime refreshEnd = date.atTime(23, 59, 59);
        
        return surgeryRepository.findByOwnerIdAndScheduledStartTimeBetween(ownerId, refreshStart, refreshEnd)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats(LocalDate date) {
        Long ownerId = getOwnerId();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        
        List<Surgery> todaySurgeries = surgeryRepository.findByOwnerIdAndScheduledStartTimeBetween(ownerId, start, end);
        
        long totalProcedures = todaySurgeries.size();
        long otInUse = todaySurgeries.stream()
                .filter(s -> s.getStatus() == Surgery.SurgeryStatus.IN_PROGRESS)
                .map(Surgery::getOtCode)
                .distinct()
                .count();

        // Avg prep time mock
        long avgPrepTime = 25; 

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProcedures", totalProcedures);
        stats.put("otInUse", otInUse);
        stats.put("avgPrepTime", avgPrepTime);
        
        return stats;
    }

    private SurgeryDTO toDTO(Surgery s) {
        return SurgeryDTO.builder()
                .id(s.getId())
                .otCode(s.getOtCode())
                .scheduledStartTime(s.getScheduledStartTime())
                .scheduledEndTime(s.getScheduledEndTime())
                .patientName(s.getPatient().getName())
                .patientId(s.getPatient().getId())
                .uhid(s.getPatient().getUhid())
                .departmentName(s.getDepartmentName() != null ? s.getDepartmentName() : (s.getLeadDoctor().getDepartment() != null ? s.getLeadDoctor().getDepartment().getName() : ""))
                .procedureName(s.getProcedureName())
                .leadDoctorName(s.getLeadDoctor().getUser().getName())
                .anaesthetistName(s.getAnaesthetistName())
                .status(s.getStatus().name())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SurgeryDTO> getSurgeriesByPatient(Long patientId) {
        Long ownerId = getOwnerId();
        return surgeryRepository.findByPatientIdAndOwnerIdOrderByScheduledStartTimeDesc(patientId, ownerId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
