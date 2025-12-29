package com.vijay.User_Master.dto.pharmacy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DispensingDTO {
    private Long id;
    private Long prescriptionId;
    private String patientName;
    private String patientUhid;
    private String patientAge;
    private String patientGender;
    private String patientPhone;
    private String doctorName;
    private LocalDateTime prescriptionDate;
    private LocalDateTime dispensedDate;
    private String dispensedBy;
    private String status;
    private BigDecimal totalAmount;
    private String notes;
    private List<DispensedItemDTO> items;
}
