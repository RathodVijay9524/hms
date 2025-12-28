package com.vijay.User_Master.dto.lab;

import com.vijay.User_Master.entity.Patient.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDTO {
    private Long id;
    private String uhid;
    private String name;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private String emergencyContactName;
    private String emergencyContactRelationship;
    private String emergencyContactPhone;
    private String emergencyContactAlternatePhone;
    private String title;
    private String bloodGroup;
    private Integer age;
    private String createdDate;
}
