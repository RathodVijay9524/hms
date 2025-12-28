package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "doctor_visits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorVisit extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String visitNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    private LocalDateTime visitDate;

    @Enumerated(EnumType.STRING)
    private VisitType visitType;

    @Column(columnDefinition = "TEXT")
    private String symptoms;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String doctorNotes;

    @Column(columnDefinition = "TEXT")
    private String subjectiveNotes;

    @Column(columnDefinition = "TEXT")
    private String objectiveNotes;

    @Column(columnDefinition = "TEXT")
    private String assessmentNotes;

    @Column(columnDefinition = "TEXT")
    private String planNotes;

    @Column(name = "doctor_name")
    private String doctorName;

    // Visit Lifecycle Management
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VisitStatus status = VisitStatus.CREATED;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "closed_by")
    private String closedBy;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    @Column(name = "locked_by")
    private String lockedBy;

    @OneToOne(mappedBy = "visit", cascade = CascadeType.ALL)
    private Prescription prescription;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public enum VisitType {
        OPD, EMERGENCY, FOLLOW_UP, ROUTINE_CHECKUP
    }
}
