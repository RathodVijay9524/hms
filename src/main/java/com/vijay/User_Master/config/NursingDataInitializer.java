package com.vijay.User_Master.config;

import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Initializes sample nursing data for testing the Nursing Station module.
 * This creates wards, assigns patients, and sets up sample tasks and medications.
 */
@Component
@Order(100) // Run after RoleDataInitializer
@RequiredArgsConstructor
@Slf4j
public class NursingDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WardRepository wardRepository;
    private final PatientRepository patientRepository;
    private final WardPatientAssignmentRepository assignmentRepository;
    private final NursingTaskRepository taskRepository;
    private final MedicationAdministrationRepository medicationRepository;

    @Override
    public void run(String... args) {
        try {
            // Find the first owner (hospital admin)
            User owner = userRepository.findAll().stream()
                    .filter(u -> u.getId() != null)
                    .findFirst()
                    .orElse(null);

            if (owner == null) {
                log.info("No owner found, skipping nursing data initialization");
                return;
            }

            Long ownerId = owner.getId();
            log.info("Initializing nursing data for owner ID: {}", ownerId);

            // Check if wards already exist
            if (wardRepository.findByOwnerIdAndIsDeletedFalse(ownerId).isEmpty()) {
                createSampleWards(owner);
                log.info("Sample wards created");
            }

            // Check if we have patients to assign
            List<Patient> patients = patientRepository.findByOwnerId(ownerId);
            if (!patients.isEmpty()) {
                assignPatientsToWards(ownerId, patients);
                createSampleTasks(ownerId);
                createSampleMedications(ownerId);
                log.info("Sample nursing data initialized successfully");
            } else {
                log.info("No patients found, skipping patient assignments");
            }

        } catch (Exception e) {
            log.error("Error initializing nursing data: {}", e.getMessage(), e);
        }
    }

    private void createSampleWards(User owner) {
        String[] wardNames = {"ICU", "General Ward", "Pediatrics", "Maternity", "Emergency"};
        String[] wardCodes = {"ICU-01", "GW-01", "PED-01", "MAT-01", "ER-01"};

        for (int i = 0; i < wardNames.length; i++) {
            Ward ward = new Ward();
            ward.setName(wardNames[i]);
            ward.setCode(wardCodes[i]);
            ward.setOwner(owner);
            ward.setIsActive(true);
            wardRepository.save(ward);
        }
    }

    private void assignPatientsToWards(Long ownerId, List<Patient> patients) {
        List<Ward> wards = wardRepository.findByOwnerIdAndIsDeletedFalse(ownerId);
        if (wards.isEmpty()) return;

        // Assign first few patients to the first ward
        Ward firstWard = wards.get(0);
        int bedNumber = 101;

        for (int i = 0; i < Math.min(5, patients.size()); i++) {
            Patient patient = patients.get(i);
            
            // Check if already assigned
            boolean alreadyAssigned = assignmentRepository
                    .findByPatientIdAndOwnerIdAndIsDeletedFalseAndStatus(
                            patient.getId(), 
                            ownerId,
                            WardPatientAssignment.AssignmentStatus.ACTIVE)
                    .stream()
                    .findFirst()
                    .isPresent();

            if (!alreadyAssigned) {
                WardPatientAssignment assignment = new WardPatientAssignment();
                assignment.setWard(firstWard);
                assignment.setPatient(patient);
                assignment.setBedCode(firstWard.getCode() + "-" + bedNumber++);
                assignment.setStatus(WardPatientAssignment.AssignmentStatus.ACTIVE);
                assignmentRepository.save(assignment);
            }
        }
    }

    private void createSampleTasks(Long ownerId) {
        List<Ward> wards = wardRepository.findByOwnerIdAndIsDeletedFalse(ownerId);
        if (wards.isEmpty()) return;

        Ward ward = wards.get(0);
        List<WardPatientAssignment> assignments = assignmentRepository
                .findByWardIdAndOwnerIdAndIsDeletedFalseAndStatus(
                        ward.getId(), ownerId, WardPatientAssignment.AssignmentStatus.ACTIVE);

        if (assignments.isEmpty()) return;

        // Create sample tasks
        String[] taskTitles = {
                "Morning Vitals Check",
                "Medication Round",
                "Wound Dressing Change",
                "Patient Assessment",
                "IV Line Check"
        };

        String[] taskDescriptions = {
                "Record BP, Temperature, Pulse, and SpO2 for all patients",
                "Administer scheduled medications as per MAR",
                "Change dressing and check for signs of infection",
                "Complete comprehensive patient assessment",
                "Check IV site for signs of infiltration or phlebitis"
        };

        NursingTask.Priority[] priorities = {
                NursingTask.Priority.HIGH,
                NursingTask.Priority.HIGH,
                NursingTask.Priority.MEDIUM,
                NursingTask.Priority.MEDIUM,
                NursingTask.Priority.LOW
        };

        for (int i = 0; i < taskTitles.length; i++) {
            // Check if task already exists
            boolean exists = taskRepository.existsByWardIdAndTitleAndIsDeletedFalse(
                    ward.getId(), taskTitles[i]);

            if (!exists) {
                NursingTask task = new NursingTask();
                task.setWard(ward);
                task.setTitle(taskTitles[i]);
                task.setDescription(taskDescriptions[i]);
                task.setPriority(priorities[i]);
                task.setShift("SHIFT_A");
                task.setStatus(NursingTask.TaskStatus.PENDING);
                task.setDueAt(LocalDateTime.now().plusHours(i + 1));
                
                // Assign some tasks to specific patients
                if (i < 2 && !assignments.isEmpty()) {
                    task.setAssignment(assignments.get(i % assignments.size()));
                }
                
                taskRepository.save(task);
            }
        }
    }

    private void createSampleMedications(Long ownerId) {
        List<Ward> wards = wardRepository.findByOwnerIdAndIsDeletedFalse(ownerId);
        if (wards.isEmpty()) return;

        Ward ward = wards.get(0);
        List<WardPatientAssignment> assignments = assignmentRepository
                .findByWardIdAndOwnerIdAndIsDeletedFalseAndStatus(
                        ward.getId(), ownerId, WardPatientAssignment.AssignmentStatus.ACTIVE);

        if (assignments.isEmpty()) return;

        // Create sample medications for the first patient
        WardPatientAssignment assignment = assignments.get(0);

        String[] medications = {"Paracetamol 500mg", "Amoxicillin 250mg", "Metformin 500mg"};
        String[] routes = {"Oral", "Oral", "Oral"};
        String[] dosages = {"1 tablet", "2 tablets", "1 tablet"};

        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < medications.length; i++) {
            // Check if medication already exists for today
            boolean exists = medicationRepository.existsByAssignmentIdAndMedicineNameAndScheduledAtBetween(
                    assignment.getId(),
                    medications[i],
                    now.toLocalDate().atStartOfDay(),
                    now.toLocalDate().atTime(23, 59, 59)
            );

            if (!exists) {
                MedicationAdministration med = new MedicationAdministration();
                med.setAssignment(assignment);
                med.setMedicineName(medications[i]);
                med.setDosage(dosages[i]);
                med.setRoute(routes[i]);
                med.setScheduledAt(now.plusHours(i + 1));
                med.setStatus(MedicationAdministration.AdminStatus.DUE);
                medicationRepository.save(med);
            }
        }
    }
}
