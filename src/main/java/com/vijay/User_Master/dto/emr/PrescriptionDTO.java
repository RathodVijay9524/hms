package com.vijay.User_Master.dto.emr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDTO {
    private Long id;
    private Long visitId;
    private String visitDate; // Formatted date
    private String doctorName;
    private List<MedicationItemDTO> medications;
    private String pharmacistNotes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicationItemDTO {
        private String medicineName;
        private String dosage;
        private String frequency;
        private String duration;
        private String instructions;
    }
}
