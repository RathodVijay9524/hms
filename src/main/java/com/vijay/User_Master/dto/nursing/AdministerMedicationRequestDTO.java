package com.vijay.User_Master.dto.nursing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdministerMedicationRequestDTO {
    private String status; // DONE/SKIPPED
    private String notes;
    private LocalDateTime administeredAt;
}
