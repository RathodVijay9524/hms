package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "outsourced_samples")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutsourcedSample extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sampleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_order_id")
    private LabOrder labOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private ReferenceLabPartner partner;

    @Column(nullable = false)
    private String testName;

    private String testCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SampleStatus status = SampleStatus.PENDING;

    private LocalDateTime sentDateTime;

    private LocalDateTime receivedByPartnerDateTime;

    private LocalDateTime resultReceivedDateTime;

    private String trackingNumber;

    private String courierName;

    @Column(length = 2000)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Builder.Default
    private Boolean isDeleted = false;

    public enum SampleStatus {
        PENDING, IN_TRANSIT, RECEIVED, PROCESSING, COMPLETED, REJECTED
    }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
}
