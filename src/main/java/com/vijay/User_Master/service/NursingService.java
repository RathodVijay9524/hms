package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.nursing.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface NursingService {

    List<WardDTO> getWards();

    WardDTO createWard(WardDTO dto);

    WardPatientDTO assignPatientToWard(AssignPatientRequestDTO dto);

    NursingDashboardDTO getDashboard(Long wardId, String shift, LocalDateTime now);

    List<WardPatientDTO> getWardPatients(Long wardId);

    void saveBatchVitals(Long wardId, List<BatchVitalEntryDTO> entries);

    List<NursingTaskDTO> getTasks(Long wardId, String shift);

    NursingTaskDTO createTask(CreateNursingTaskRequestDTO dto);

    NursingTaskDTO completeTask(Long taskId);

    List<MedicationAdministrationDTO> getMedicationsForWard(Long wardId, LocalDate date);

    MedicationAdministrationDTO administerMedication(Long administrationId, AdministerMedicationRequestDTO dto);

    NursingHandoverDTO getHandover(Long wardId, LocalDate date, String fromShift, String toShift);

    NursingHandoverDTO saveHandover(NursingHandoverDTO dto);

    List<NursingAlertDTO> getOpenAlerts(Long wardId);

    NursingAlertDTO acknowledgeAlert(Long alertId);
}
