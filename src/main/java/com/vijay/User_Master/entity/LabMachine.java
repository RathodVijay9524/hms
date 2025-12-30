package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_machines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabMachine extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String machineName;

    private String manufacturer;

    private String modelNumber;

    private String serialNumber;

    @Column(nullable = false)
    private String machineType; // Biochemistry Analyzer, Hematology Analyzer, etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MachineStatus status = MachineStatus.ONLINE;

    private LocalDateTime lastCalibrationDate;

    private LocalDateTime nextCalibrationDue;

    private Integer calibrationIntervalDays;

    private String location;

    private String errorCode;

    private String errorDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Builder.Default
    private Boolean isDeleted = false;

    public enum MachineStatus {
        ONLINE, OFFLINE, MAINTENANCE, CALIBRATING, ERROR
    }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
}
