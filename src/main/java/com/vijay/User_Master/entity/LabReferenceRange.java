package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lab_reference_ranges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabReferenceRange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parameter_id", nullable = false)
    private LabParameter parameter;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Integer minAgeYears;
    private Integer maxAgeYears;

    private Double lowerLimit;
    private Double upperLimit;

    private Double criticalLow;
    private Double criticalHigh;

    public enum Gender {
        MALE, FEMALE, BOTH;

        @com.fasterxml.jackson.annotation.JsonCreator
        public static Gender fromString(String value) {
            if (value == null) return null;
            return Gender.valueOf(value.toUpperCase());
        }
    }
}
