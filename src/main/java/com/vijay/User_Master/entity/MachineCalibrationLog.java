package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "machine_calibration_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MachineCalibrationLog extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", nullable = false)
    private LabMachine machine;

    @Column(nullable = false)
    private LocalDateTime calibrationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CalibrationType calibrationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CalibrationResult result = CalibrationResult.PENDING;

    @Column(length = 2000)
    private String notes;

    private String performedBy;

    private Integer parametersChecked;

    private Integer parametersInRange;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Builder.Default
    private Boolean isDeleted = false;

    public enum CalibrationType {
        DAILY_QC, CALIBRATION, MAINTENANCE, PRIMING, CLEANING
    }

    public enum CalibrationResult {
        PENDING, PASS, FAIL, WARNING
    }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
}
