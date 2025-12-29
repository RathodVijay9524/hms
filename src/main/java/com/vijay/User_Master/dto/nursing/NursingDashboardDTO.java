package com.vijay.User_Master.dto.nursing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NursingDashboardDTO {
    private Long wardPatients;
    private Long medsDue;
    private Long vitalsPending;
    private Long criticalAlerts;

    private List<NursingPatientCardDTO> patientWatchlist;
    private List<MedicationDueDTO> medicationSchedule;
}
