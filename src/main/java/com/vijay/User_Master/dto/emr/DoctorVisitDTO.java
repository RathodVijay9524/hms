package com.vijay.User_Master.dto.emr;

import com.vijay.User_Master.entity.DoctorVisit;
import com.vijay.User_Master.entity.VisitStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorVisitDTO {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private String doctorName;
    private LocalDateTime visitDate;
    private DoctorVisit.VisitType visitType;
    private String symptoms;
    private String diagnosis;
    private String doctorNotes;
    
    // Visit Lifecycle
    private VisitStatus status;
    private LocalDateTime closedAt;
    private String closedBy;
    private LocalDateTime lockedAt;
    private String lockedBy;
    
    private boolean hasPrescription;
    private Long prescriptionId;
}
