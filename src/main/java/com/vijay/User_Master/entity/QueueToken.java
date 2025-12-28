package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "queue_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueueToken extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String tokenNumber; // e.g., T-101

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient; // Can be null for walk-ins without registration yet

    private String patientName; // For quick reference / walk-ins
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private DoctorProfile doctor;

    @Column(nullable = false)
    private LocalDate tokenDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TokenStatus status = TokenStatus.WAITING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public enum TokenStatus {
        WAITING, CALLED, IN_CONSULTATION, COMPLETED, CANCELLED, SKIPPED
    }
}
