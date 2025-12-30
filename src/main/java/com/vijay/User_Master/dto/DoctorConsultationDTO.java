package com.vijay.User_Master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorConsultationDTO {
    private Long id;
    private Long patientId;
    private String patientName;
    private String uhid;
    
    private String requesterName;
    private String requesterSpecialization;
    
    private String recipientName;
    private String reason;
    private String clinicalNotes;
    
    private String urgency;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime respondedAt;
    
    private boolean isIncoming; // Helper for frontend
}
