package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a patient symptom recorded during a visit
 */
@Entity
@Table(name = "patient_symptoms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientSymptom extends BaseModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    private DoctorVisit visit;
    
    @Column(nullable = false, length = 100)
    private String symptomName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Severity severity;
    
    @Column(length = 50)
    private String duration; // e.g., "2 days", "1 week"
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}
