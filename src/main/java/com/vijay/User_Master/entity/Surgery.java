package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "surgeries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Surgery extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_doctor_id", nullable = false)
    private DoctorProfile leadDoctor;

    @Column(name = "ot_code", nullable = false)
    private String otCode; // e.g. "OT-01"

    @Column(name = "procedure_name", nullable = false)
    private String procedureName;

    @Column(name = "department_name")
    private String departmentName; // e.g. "Orthopedic"

    @Column(name = "scheduled_start_time", nullable = false)
    private LocalDateTime scheduledStartTime;

    @Column(name = "scheduled_end_time")
    private LocalDateTime scheduledEndTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SurgeryStatus status = SurgeryStatus.SCHEDULED;

    @Column(name = "anaesthetist_name")
    private String anaesthetistName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public enum SurgeryStatus {
        SCHEDULED,
        PREP_READY,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}
