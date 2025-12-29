package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.nursing.*;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.NursingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NursingServiceImpl implements NursingService {

    private final UserRepository userRepository;
    private final WardRepository wardRepository;
    private final WardPatientAssignmentRepository wardPatientAssignmentRepository;
    private final PatientRepository patientRepository;
    private final VitalSignRepository vitalSignRepository;
    private final NursingTaskRepository nursingTaskRepository;
    private final MedicationAdministrationRepository medicationAdministrationRepository;
    private final NursingHandoverRepository nursingHandoverRepository;
    private final NursingAlertRepository nursingAlertRepository;

    private Long getOwnerId() {
        return CommonUtils.getLoggedInUser().getOwnerId();
    }

    private User getOwner() {
        return userRepository.findById(getOwnerId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WardDTO> getWards() {
        Long ownerId = getOwnerId();
        return wardRepository.findByOwnerIdAndIsDeletedFalse(ownerId).stream()
                .filter(w -> Boolean.TRUE.equals(w.getIsActive()))
                .map(w -> new WardDTO(w.getId(), w.getName(), w.getCode()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WardDTO createWard(WardDTO dto) {
        User owner = getOwner();
        Ward ward = new Ward();
        ward.setName(dto.getName());
        ward.setCode(dto.getCode());
        ward.setOwner(owner);
        Ward saved = wardRepository.save(ward);
        return new WardDTO(saved.getId(), saved.getName(), saved.getCode());
    }

    @Override
    @Transactional
    public WardPatientDTO assignPatientToWard(AssignPatientRequestDTO dto) {
        Long ownerId = getOwnerId();
        Ward ward = wardRepository.findByIdAndOwnerId(dto.getWardId(), ownerId)
                .orElseThrow(() -> new RuntimeException("Ward not found"));
        Patient patient = patientRepository.findByIdAndOwnerId(dto.getPatientId(), ownerId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        WardPatientAssignment assignment = new WardPatientAssignment();
        assignment.setWard(ward);
        assignment.setPatient(patient);
        assignment.setBedCode(dto.getBedCode());
        assignment.setStatus(WardPatientAssignment.AssignmentStatus.ACTIVE);
        WardPatientAssignment saved = wardPatientAssignmentRepository.save(assignment);

        return toWardPatientDTO(saved, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public NursingDashboardDTO getDashboard(Long wardId, String shift, LocalDateTime now) {
        Long ownerId = getOwnerId();
        LocalDateTime windowFrom = now;
        LocalDateTime windowTo = now.plusHours(2);

        List<WardPatientAssignment> assignments = wardPatientAssignmentRepository
                .findByWardIdAndOwnerIdAndIsDeletedFalseAndStatus(wardId, ownerId, WardPatientAssignment.AssignmentStatus.ACTIVE);

        long wardPatients = assignments.size();

        long medsDue = medicationAdministrationRepository.countForWardDateAndStatus(ownerId, wardId, now.toLocalDate(), MedicationAdministration.AdminStatus.DUE);
        long criticalAlerts = nursingAlertRepository.countCriticalOpen(ownerId, wardId);

        long vitalsPending = assignments.stream()
                .filter(a -> {
                    Optional<VitalSign> last = vitalSignRepository.findByPatientIdAndOwnerIdOrderByRecordedAtDesc(a.getPatient().getId(), ownerId).stream().findFirst();
                    if (last.isEmpty()) return true;
                    LocalDateTime recordedAt = last.get().getRecordedAt();
                    if (recordedAt == null) return true;
                    return recordedAt.isBefore(now.minusHours(4));
                })
                .count();

        List<NursingPatientCardDTO> watchlist = assignments.stream()
                .limit(5)
                .map(a -> {
                    String bp = getLastBpSummary(a.getPatient().getId(), ownerId);
                    return NursingPatientCardDTO.builder()
                            .assignmentId(a.getId())
                            .patientId(a.getPatient().getId())
                            .patientName(a.getPatient().getName())
                            .uhid(a.getPatient().getUhid())
                            .bedCode(a.getBedCode())
                            .stability(deriveStability(bp))
                            .bpSummary(bp)
                            .build();
                })
                .collect(Collectors.toList());

        List<MedicationDueDTO> schedule = medicationAdministrationRepository
                .findForWardWindow(ownerId, wardId, windowFrom, windowTo)
                .stream()
                .filter(ma -> ma.getStatus() == MedicationAdministration.AdminStatus.DUE)
                .sorted(Comparator.comparing(MedicationAdministration::getScheduledAt))
                .limit(10)
                .map(ma -> MedicationDueDTO.builder()
                        .administrationId(ma.getId())
                        .scheduledAt(ma.getScheduledAt())
                        .assignmentId(ma.getAssignment().getId())
                        .patientName(ma.getAssignment().getPatient().getName())
                        .bedCode(ma.getAssignment().getBedCode())
                        .medicineName(ma.getMedicineName())
                        .dosage(ma.getDosage())
                        .route(ma.getRoute())
                        .build())
                .collect(Collectors.toList());

        return NursingDashboardDTO.builder()
                .wardPatients(wardPatients)
                .medsDue(medsDue)
                .vitalsPending(vitalsPending)
                .criticalAlerts(criticalAlerts)
                .patientWatchlist(watchlist)
                .medicationSchedule(schedule)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WardPatientDTO> getWardPatients(Long wardId) {
        Long ownerId = getOwnerId();
        return wardPatientAssignmentRepository
                .findByWardIdAndOwnerIdAndIsDeletedFalseAndStatus(wardId, ownerId, WardPatientAssignment.AssignmentStatus.ACTIVE)
                .stream()
                .map(a -> toWardPatientDTO(a, getLastBpSummary(a.getPatient().getId(), ownerId), null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveBatchVitals(Long wardId, List<BatchVitalEntryDTO> entries) {
        Long ownerId = getOwnerId();
        User owner = getOwner();

        for (BatchVitalEntryDTO e : entries) {
            Patient patient = patientRepository.findByIdAndOwnerId(e.getPatientId(), ownerId)
                    .orElseThrow(() -> new RuntimeException("Patient not found"));

            VitalSign vs = new VitalSign();
            vs.setPatient(patient);
            vs.setOwner(owner);
            vs.setRecordedAt(LocalDateTime.now());

            if (e.getBp() != null && e.getBp().contains("/")) {
                String[] parts = e.getBp().split("/");
                if (parts.length == 2) {
                    try {
                        vs.setSystolicBP(Integer.parseInt(parts[0].trim()));
                        vs.setDiastolicBP(Integer.parseInt(parts[1].trim()));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            vs.setTemperature(e.getTemperature());
            vs.setHeartRate(e.getPulse());
            vs.setOxygenLevel(e.getOxygenLevel());

            vitalSignRepository.save(vs);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NursingTaskDTO> getTasks(Long wardId, String shift) {
        Long ownerId = getOwnerId();
        return nursingTaskRepository.findByWardIdAndOwnerIdAndIsDeletedFalseAndShift(wardId, ownerId, shift)
                .stream()
                .sorted(Comparator.comparing(NursingTask::getDueAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toTaskDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NursingTaskDTO createTask(CreateNursingTaskRequestDTO dto) {
        Long ownerId = getOwnerId();
        Ward ward = wardRepository.findByIdAndOwnerId(dto.getWardId(), ownerId)
                .orElseThrow(() -> new RuntimeException("Ward not found"));

        WardPatientAssignment assignment = null;
        if (dto.getAssignmentId() != null) {
            assignment = wardPatientAssignmentRepository.findByIdAndOwnerId(dto.getAssignmentId(), ownerId)
                    .orElseThrow(() -> new RuntimeException("Assignment not found"));
        }

        NursingTask task = new NursingTask();
        task.setWard(ward);
        task.setAssignment(assignment);
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setShift(dto.getShift());
        task.setDueAt(dto.getDueAt());

        if (dto.getPriority() != null) {
            task.setPriority(NursingTask.Priority.valueOf(dto.getPriority().toUpperCase()));
        }

        NursingTask saved = nursingTaskRepository.save(task);
        return toTaskDTO(saved);
    }

    @Override
    @Transactional
    public NursingTaskDTO completeTask(Long taskId) {
        Long ownerId = getOwnerId();
        NursingTask task = nursingTaskRepository.findByIdAndOwnerId(taskId, ownerId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(NursingTask.TaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        task.setCompletedBy(CommonUtils.getLoggedInUser().getId());
        NursingTask saved = nursingTaskRepository.save(task);
        return toTaskDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicationAdministrationDTO> getMedicationsForWard(Long wardId, LocalDate date) {
        Long ownerId = getOwnerId();
        return medicationAdministrationRepository.findForWardDate(ownerId, wardId, date).stream()
                .sorted(Comparator.comparing(MedicationAdministration::getScheduledAt))
                .map(this::toMedicationDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MedicationAdministrationDTO administerMedication(Long administrationId, AdministerMedicationRequestDTO dto) {
        Long ownerId = getOwnerId();
        MedicationAdministration ma = medicationAdministrationRepository.findByIdAndOwnerId(administrationId, ownerId)
                .orElseThrow(() -> new RuntimeException("Medication administration not found"));

        if (dto.getStatus() != null) {
            ma.setStatus(MedicationAdministration.AdminStatus.valueOf(dto.getStatus().toUpperCase()));
        } else {
            ma.setStatus(MedicationAdministration.AdminStatus.DONE);
        }

        ma.setNotes(dto.getNotes());
        ma.setAdministeredBy(CommonUtils.getLoggedInUser().getId());
        ma.setAdministeredAt(dto.getAdministeredAt() != null ? dto.getAdministeredAt() : LocalDateTime.now());

        MedicationAdministration saved = medicationAdministrationRepository.save(ma);
        return toMedicationDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public NursingHandoverDTO getHandover(Long wardId, LocalDate date, String fromShift, String toShift) {
        Long ownerId = getOwnerId();
        NursingHandover handover = nursingHandoverRepository
                .findByOwnerIdAndWardIdAndHandoverDateAndFromShiftAndToShift(ownerId, wardId, date, fromShift, toShift)
                .orElseGet(() -> {
                    Ward ward = wardRepository.findByIdAndOwnerId(wardId, ownerId)
                            .orElseThrow(() -> new RuntimeException("Ward not found"));
                    NursingHandover h = new NursingHandover();
                    h.setWard(ward);
                    h.setHandoverDate(date);
                    h.setFromShift(fromShift);
                    h.setToShift(toShift);
                    return nursingHandoverRepository.save(h);
                });

        return toHandoverDTO(handover);
    }

    @Override
    @Transactional
    public NursingHandoverDTO saveHandover(NursingHandoverDTO dto) {
        Long ownerId = getOwnerId();
        NursingHandover handover;

        if (dto.getId() != null) {
            handover = nursingHandoverRepository.findByIdAndOwnerId(dto.getId(), ownerId)
                    .orElseThrow(() -> new RuntimeException("Handover not found"));
        } else {
            Ward ward = wardRepository.findByIdAndOwnerId(dto.getWardId(), ownerId)
                    .orElseThrow(() -> new RuntimeException("Ward not found"));
            handover = new NursingHandover();
            handover.setWard(ward);
            handover.setHandoverDate(dto.getHandoverDate());
            handover.setFromShift(dto.getFromShift());
            handover.setToShift(dto.getToShift());
        }

        handover.setReportText(dto.getReportText());
        handover.setChecklistJson(dto.getChecklistJson());

        if (dto.getSignedOffAt() != null || dto.getSignedOffBy() != null) {
            handover.setSignedOffAt(dto.getSignedOffAt() != null ? dto.getSignedOffAt() : LocalDateTime.now());
            handover.setSignedOffBy(dto.getSignedOffBy() != null ? dto.getSignedOffBy() : CommonUtils.getLoggedInUser().getId());
        }

        NursingHandover saved = nursingHandoverRepository.save(handover);
        return toHandoverDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NursingAlertDTO> getOpenAlerts(Long wardId) {
        Long ownerId = getOwnerId();
        return nursingAlertRepository.findByOwnerIdAndWardIdAndIsAcknowledgedFalse(ownerId, wardId).stream()
                .sorted(Comparator.comparing(NursingAlert::getCreatedDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toAlertDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NursingAlertDTO acknowledgeAlert(Long alertId) {
        Long ownerId = getOwnerId();
        NursingAlert alert = nursingAlertRepository.findByIdAndOwnerId(alertId, ownerId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));

        alert.setIsAcknowledged(true);
        alert.setAcknowledgedAt(LocalDateTime.now());
        alert.setAcknowledgedBy(CommonUtils.getLoggedInUser().getId());

        NursingAlert saved = nursingAlertRepository.save(alert);
        return toAlertDTO(saved);
    }

    private NursingTaskDTO toTaskDTO(NursingTask t) {
        return NursingTaskDTO.builder()
                .id(t.getId())
                .wardId(t.getWard() != null ? t.getWard().getId() : null)
                .assignmentId(t.getAssignment() != null ? t.getAssignment().getId() : null)
                .title(t.getTitle())
                .description(t.getDescription())
                .priority(t.getPriority() != null ? t.getPriority().name() : null)
                .status(t.getStatus() != null ? t.getStatus().name() : null)
                .shift(t.getShift())
                .dueAt(t.getDueAt())
                .completedAt(t.getCompletedAt())
                .build();
    }

    private MedicationAdministrationDTO toMedicationDTO(MedicationAdministration ma) {
        return MedicationAdministrationDTO.builder()
                .id(ma.getId())
                .wardId(ma.getAssignment() != null && ma.getAssignment().getWard() != null ? ma.getAssignment().getWard().getId() : null)
                .assignmentId(ma.getAssignment() != null ? ma.getAssignment().getId() : null)
                .medicineName(ma.getMedicineName())
                .dosage(ma.getDosage())
                .route(ma.getRoute())
                .instructions(ma.getInstructions())
                .scheduledAt(ma.getScheduledAt())
                .status(ma.getStatus() != null ? ma.getStatus().name() : null)
                .administeredAt(ma.getAdministeredAt())
                .notes(ma.getNotes())
                .build();
    }

    private NursingHandoverDTO toHandoverDTO(NursingHandover h) {
        return NursingHandoverDTO.builder()
                .id(h.getId())
                .wardId(h.getWard() != null ? h.getWard().getId() : null)
                .handoverDate(h.getHandoverDate())
                .fromShift(h.getFromShift())
                .toShift(h.getToShift())
                .reportText(h.getReportText())
                .checklistJson(h.getChecklistJson())
                .signedOffBy(h.getSignedOffBy())
                .signedOffAt(h.getSignedOffAt())
                .build();
    }

    private NursingAlertDTO toAlertDTO(NursingAlert a) {
        return NursingAlertDTO.builder()
                .id(a.getId())
                .wardId(a.getWard() != null ? a.getWard().getId() : null)
                .assignmentId(a.getAssignment() != null ? a.getAssignment().getId() : null)
                .alertType(a.getAlertType() != null ? a.getAlertType().name() : null)
                .severity(a.getSeverity() != null ? a.getSeverity().name() : null)
                .message(a.getMessage())
                .isAcknowledged(a.getIsAcknowledged())
                .acknowledgedAt(a.getAcknowledgedAt())
                .createdDate(a.getCreatedDate())
                .build();
    }

    private WardPatientDTO toWardPatientDTO(WardPatientAssignment a, String bpSummary, String primaryDoctorName) {
        Patient p = a.getPatient();
        Integer age = null;
        if (p.getDateOfBirth() != null) {
            age = Period.between(p.getDateOfBirth(), LocalDate.now()).getYears();
        }

        String gender = p.getGender() != null ? p.getGender().name() : null;
        String bp = bpSummary != null ? bpSummary : getLastBpSummary(p.getId(), getOwnerId());

        return WardPatientDTO.builder()
                .assignmentId(a.getId())
                .patientId(p.getId())
                .patientName(p.getName())
                .uhid(p.getUhid())
                .bedCode(a.getBedCode())
                .gender(gender)
                .age(age)
                .primaryDoctorName(primaryDoctorName)
                .lastAction(null)
                .stability(deriveStability(bp))
                .bpSummary(bp)
                .build();
    }

    private String getLastBpSummary(Long patientId, Long ownerId) {
        return vitalSignRepository.findByPatientIdAndOwnerIdOrderByRecordedAtDesc(patientId, ownerId).stream()
                .findFirst()
                .map(v -> {
                    if (v.getSystolicBP() != null && v.getDiastolicBP() != null) {
                        return v.getSystolicBP() + "/" + v.getDiastolicBP();
                    }
                    return null;
                })
                .orElse(null);
    }

    private String deriveStability(String bpSummary) {
        if (bpSummary == null || !bpSummary.contains("/")) {
            return "OBSERVATION";
        }
        try {
            String[] parts = bpSummary.split("/");
            int sys = Integer.parseInt(parts[0].trim());
            int dia = Integer.parseInt(parts[1].trim());
            if (sys >= 160 || dia >= 100) return "CRITICAL";
            if (sys >= 140 || dia >= 90) return "OBSERVATION";
            return "STABLE";
        } catch (Exception e) {
            return "OBSERVATION";
        }
    }
}
