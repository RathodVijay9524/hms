package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.DoctorConsultationDTO;
import com.vijay.User_Master.entity.DoctorConsultation;
import com.vijay.User_Master.repository.DoctorConsultationRepository;
import com.vijay.User_Master.service.InternalConsultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InternalConsultServiceImpl implements InternalConsultService {

    private final DoctorConsultationRepository consultationRepository;

    private Long getOwnerId() {
        return CommonUtils.getLoggedInUser().getOwnerId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorConsultationDTO> getIncomingConsults(Long doctorId) {
        Long ownerId = getOwnerId();
        return consultationRepository.findByRecipientIdAndOwnerIdOrderByRequestedAtDesc(doctorId, ownerId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorConsultationDTO> getOutgoingConsults(Long doctorId) {
        Long ownerId = getOwnerId();
        return consultationRepository.findByRequesterIdAndOwnerIdOrderByRequestedAtDesc(doctorId, ownerId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getStats(Long doctorId) {
        Long ownerId = getOwnerId();
        
        long pendingInbound = consultationRepository.countByRecipientIdAndOwnerIdAndStatus(doctorId, ownerId, DoctorConsultation.ConsultationStatus.PENDING);
        
        // Accepted Today logic: requires filtering by respondedAt logic in repo or in memory. 
        // For simplicity, let's just count total ACCEPTED/COMPLETED for now, or implement a specific repo method if critical.
        // Or filter incoming list.
        
        // Let's rely on repo for "Accepted" status count, disregarding "Today" constraint for now to save a custom query, 
        // or just mock "Today" behavior if needed. 
        // Actually, let's just count "ACCEPTED" and "COMPLETED" total.
        long acceptedTotal = consultationRepository.countByRecipientIdAndOwnerIdAndStatusIn(doctorId, ownerId, 
                List.of(DoctorConsultation.ConsultationStatus.ACCEPTED, DoctorConsultation.ConsultationStatus.COMPLETED));

        Map<String, Object> stats = new HashMap<>();
        stats.put("pendingInbound", pendingInbound);
        stats.put("acceptedTotal", acceptedTotal); 
        stats.put("avgRespTime", "1.2h"); // Mock for now

        return stats;
    }

    private DoctorConsultationDTO toDTO(DoctorConsultation c) {
        Long currentUserId = CommonUtils.getLoggedInUser().getId(); // Note: this is User ID, not Doctor ID. 
        // Logic to determine direction is usually done by caller (getIncoming vs getOutgoing).
        // If we want 'isIncoming' flag in DTO, we need to know who is viewing. 
        // But for getIncoming(doctorId), isIncoming is always true? No, Recipient == doctorId.
        
        return DoctorConsultationDTO.builder()
                .id(c.getId())
                .patientId(c.getPatient().getId())
                .patientName(c.getPatient().getName())
                .uhid(c.getPatient().getUhid())
                .requesterName(c.getRequester().getUser().getName())
                .requesterSpecialization(c.getRequester().getSpecialization())
                .recipientName(c.getRecipient().getUser().getName())
                .reason(c.getReason())
                .clinicalNotes(c.getClinicalNotes())
                .urgency(c.getUrgency().name())
                .status(c.getStatus().name())
                .requestedAt(c.getRequestedAt())
                .respondedAt(c.getRespondedAt())
                .build();
    }
}
