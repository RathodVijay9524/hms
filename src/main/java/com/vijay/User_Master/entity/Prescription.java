package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prescriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    private DoctorVisit visit;

    @ElementCollection
    @CollectionTable(name = "prescription_medications", joinColumns = @JoinColumn(name = "prescription_id"))
    @Builder.Default
    private List<MedicationItem> medications = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String pharmacistNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MedicationItem {
        private String medicineName;
        private String dosage; // e.g., "500mg"
        private String frequency; // e.g., "1-0-1"
        private String duration; // e.g., "5 days"
        private String instructions; // e.g., "After food"
    }
}
