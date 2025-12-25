package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Appointment;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {
    private Long id;
    private String appointmentNumber;
    private Long patientId;
    private String patientName;
    private String patientUhid;
    private Long doctorId;
    private String doctorName;
    private Long slotId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private Appointment.AppointmentStatus status;
    private Long visitId;
    private String reasonForVisit;
    private String notes;
}
