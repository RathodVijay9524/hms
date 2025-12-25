package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Appointment;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequest {
    private Long patientId;
    private Long doctorId;
    private Long slotId;
    private String reasonForVisit;
    private String notes;
}
