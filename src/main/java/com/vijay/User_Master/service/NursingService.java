package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.nursing.*;

import com.vijay.User_Master.entity.Ward;
import com.vijay.User_Master.entity.WardPatientAssignment;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface NursingService {

    List<WardDTO> getWards();

    WardDTO createWard(WardDTO dto);

    WardDTO updateWard(Long id, WardDTO dto);

    void deleteWard(Long id);

    Ward getWardById(Long wardId); // Added method

    WardPatientDTO assignPatientToWard(AssignPatientRequestDTO request); // Modified return type back to DTO

    NursingDashboardDTO getDashboard(Long wardId, String shift, LocalDateTime now);

    List<WardPatientDTO> getWardPatients(Long wardId);

    void saveBatchVitals(Long wardId, List<BatchVitalEntryDTO> entries);

    List<NursingTaskDTO> getTasks(Long wardId, String shift, LocalDate date);

    NursingTaskDTO createTask(CreateNursingTaskRequestDTO dto);

    NursingTaskDTO completeTask(Long taskId);

    List<MedicationAdministrationDTO> getMedicationsForWard(Long wardId, LocalDate date);

    MedicationAdministrationDTO administerMedication(Long administrationId, AdministerMedicationRequestDTO dto);

    NursingHandoverDTO getHandover(Long wardId, LocalDate date, String fromShift, String toShift);

    NursingHandoverDTO saveHandover(NursingHandoverDTO dto);

    List<NursingAlertDTO> getOpenAlerts(Long wardId);

    NursingAlertDTO acknowledgeAlert(Long alertId);

    List<WardPatientDTO> getAdmittedPatientsForDoctor(Long doctorId);

    PatientAdmissionStatusDTO getPatientAdmissionStatus(String uhid, Long ownerId);
}
