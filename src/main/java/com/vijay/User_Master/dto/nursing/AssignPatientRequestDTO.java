package com.vijay.User_Master.dto.nursing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignPatientRequestDTO {
    private Long wardId;
    private Long patientId;
    private String bedCode;
}
