package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "lab_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabOrder extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToMany
    @JoinTable(
        name = "lab_order_tests",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "test_id")
    )
    private List<LabTest> tests;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime collectionDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner; // Tenant ID

    @Column(length = 5000)
    private String aiSummary;

    private boolean doctorVerified;

    @Column(length = 1000)
    private String doctorRemarks;

    public enum OrderStatus {
        ORDERED,
        SAMPLE_COLLECTED,
        IN_PROCESS,
        RESULT_ENTERED,
        VERIFIED,
        REPORT_READY
    }
}
