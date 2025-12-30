package com.vijay.User_Master.dto.nursing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientLiteDTO {
    private Long id;
    private String name;
    private String uhid;
    private Integer age;
}
