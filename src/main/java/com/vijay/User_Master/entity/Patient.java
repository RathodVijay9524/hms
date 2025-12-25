package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String email;

    private String phone;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String address;

    // Emergency Contact Information
    @Column(length = 100)
    private String emergencyContactName;

    @Column(length = 50)
    private String emergencyContactRelationship;

    @Column(length = 20)
    private String emergencyContactPhone;

    @Column(length = 20)
    private String emergencyContactAlternatePhone;

    @Column(unique = true, length = 20)
    private String uhid; // Unique Hospital ID: H{businessId}-{year}-{sequence}

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner; // Tenant ID

    public enum Gender {
        MALE, FEMALE, OTHER;

        @com.fasterxml.jackson.annotation.JsonCreator
        public static Gender fromString(String value) {
            if (value == null) return null;
            return Gender.valueOf(value.toUpperCase());
        }
    }
}
