package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vital_signs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VitalSign extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    private LocalDateTime recordedAt;

    private Integer systolicBP;
    private Integer diastolicBP;
    private Integer heartRate;
    private Double temperature;
    private Double weight;
    private Double height;
    private Double bmi;
    private String bmiCategory;
    private Integer oxygenLevel; // SpO2

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @PrePersist
    @PreUpdate
    public void calculateBMI() {
        if (height != null && weight != null && height > 0) {
            double heightInMeters = height / 100.0;
            this.bmi = Math.round((weight / (heightInMeters * heightInMeters)) * 10.0) / 10.0;
            this.bmiCategory = determineBMICategory(this.bmi);
        }
    }

    private String determineBMICategory(Double bmi) {
        if (bmi == null) return null;
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25) return "Normal";
        if (bmi < 30) return "Overweight";
        return "Obese";
    }
}
