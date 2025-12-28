package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.emr.*;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.PatientEMRService;
import com.vijay.User_Master.service.VisitLifecycleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientEMRServiceImpl implements PatientEMRService {

    private final MedicalHistoryRepository medicalHistoryRepository;
    private final VitalSignRepository vitalSignRepository;
    private final DoctorVisitRepository doctorVisitRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final VisitLifecycleService visitLifecycleService;
    private final com.vijay.User_Master.service.EMRAuditService emrAuditService;
    private final ModelMapper modelMapper;

    private Long getOwnerId() {
        return CommonUtils.getLoggedInUser().getOwnerId();
    }

    private Patient getPatient(Long patientId) {
        return patientRepository.findByIdAndOwnerId(patientId, getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));
    }

    @Override
    public MedicalHistoryDTO getMedicalHistory(Long patientId) {
        Long ownerId = getOwnerId();
        // Verify patient exists for this owner
        getPatient(patientId);

        return medicalHistoryRepository.findByPatientIdAndOwnerId(patientId, ownerId)
                .map(history -> modelMapper.map(history, MedicalHistoryDTO.class))
                .orElse(MedicalHistoryDTO.builder().patientId(patientId).build());
    }

    @Override
    @Transactional
    public MedicalHistoryDTO saveMedicalHistory(Long patientId, MedicalHistoryDTO historyDTO) {
        Long ownerId = getOwnerId();
        Patient patient = getPatient(patientId);
        User owner = userRepository.findById(ownerId).orElseThrow();

        MedicalHistory history = medicalHistoryRepository.findByPatientIdAndOwnerId(patientId, ownerId)
                .orElse(MedicalHistory.builder().patient(patient).owner(owner).build());

        // Capture before state for audit
        MedicalHistoryDTO beforeState = history.getId() != null ? 
            modelMapper.map(history, MedicalHistoryDTO.class) : null;
        boolean isCreate = history.getId() == null;

        history.setAllergies(historyDTO.getAllergies());
        history.setChronicConditions(historyDTO.getChronicConditions());
        history.setPastSurgeries(historyDTO.getPastSurgeries());
        history.setFamilyHistory(historyDTO.getFamilyHistory());
        history.setBloodGroup(historyDTO.getBloodGroup());

        MedicalHistory saved = medicalHistoryRepository.save(history);
        MedicalHistoryDTO result = modelMapper.map(saved, MedicalHistoryDTO.class);
        
        // Log audit
        emrAuditService.logChange(
            com.vijay.User_Master.entity.EMRAuditLog.EntityType.MEDICAL_HISTORY,
            saved.getId(),
            isCreate ? com.vijay.User_Master.entity.EMRAuditLog.AuditAction.CREATE : 
                      com.vijay.User_Master.entity.EMRAuditLog.AuditAction.UPDATE,
            beforeState,
            result
        );
        
        return result;
    }

    @Override
    @Transactional
    public VitalSignDTO addVitalSign(Long patientId, VitalSignDTO vitalSignDTO) {
        Long ownerId = getOwnerId();
        Patient patient = getPatient(patientId);
        User owner = userRepository.findById(ownerId).orElseThrow();

        VitalSign vitalSign = modelMapper.map(vitalSignDTO, VitalSign.class);
        vitalSign.setPatient(patient);
        vitalSign.setOwner(owner);
        if (vitalSign.getRecordedAt() == null) {
            vitalSign.setRecordedAt(LocalDateTime.now());
        }

        VitalSign saved = vitalSignRepository.save(vitalSign);
        return modelMapper.map(saved, VitalSignDTO.class);
    }

    @Override
    public Page<VitalSignDTO> getVitalSigns(Long patientId, int page, int size) {
        Long ownerId = getOwnerId();
        getPatient(patientId); // Validate access
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("recordedAt").descending());
        return vitalSignRepository.findByPatientIdAndOwnerId(patientId, ownerId, pageable)
                .map(v -> modelMapper.map(v, VitalSignDTO.class));
    }

    @Override
    public List<VitalSignDTO> getRecentVitalSigns(Long patientId) {
        Long ownerId = getOwnerId();
        getPatient(patientId);
        // Custom query method or Pageable with limit
        return vitalSignRepository.findByPatientIdAndOwnerIdOrderByRecordedAtDesc(patientId, ownerId).stream()
                .limit(5)
                .map(v -> modelMapper.map(v, VitalSignDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DoctorVisitDTO createVisit(Long patientId, DoctorVisitDTO visitDTO) {
        Long ownerId = getOwnerId();
        Patient patient = getPatient(patientId);
        User owner = userRepository.findById(ownerId).orElseThrow();
        
        // For now, assume the logged in user is the doctor or assign explicitly. 
        // Ideally we should pass doctorId from UI or use current user if they are a doctor.
        User doctor;
        if (visitDTO.getDoctorId() != null) {
            doctor = userRepository.findById(visitDTO.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", visitDTO.getDoctorId()));
        } else {
            Long currentUserId = CommonUtils.getLoggedInUser().getId();
            doctor = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));
        }

        DoctorVisit visit = modelMapper.map(visitDTO, DoctorVisit.class);
        visit.setPatient(patient);
        visit.setDoctor(doctor);
        visit.setOwner(owner);
        
        // Generate unique Visit Number: VN-{ownerId}-{year}-{sequence}
        if (visit.getVisitNumber() == null || visit.getVisitNumber().isEmpty()) {
            String year = String.valueOf(java.time.LocalDate.now().getYear());
            String pattern = String.format("VN%d-%s-%%", ownerId, year);
            String maxVN = doctorVisitRepository.findMaxVisitNumberByPattern(pattern, ownerId);
            int sequence = 1;
            if (maxVN != null && !maxVN.isEmpty()) {
                try {
                    String sequencePart = maxVN.substring(maxVN.lastIndexOf('-') + 1);
                    sequence = Integer.parseInt(sequencePart) + 1;
                } catch (Exception e) {
                    sequence = 1;
                }
            }
            visit.setVisitNumber(String.format("VN%d-%s-%06d", ownerId, year, sequence));
        }
        
        // Ensure default status if not provided to prevent nullable constraint violation
        if (visit.getStatus() == null) {
            visit.setStatus(VisitStatus.CREATED);
        }
        
        if (visit.getVisitDate() == null) {
            visit.setVisitDate(LocalDateTime.now());
        }

        if (visit.getSymptoms() == null) {
            visit.setSymptoms("");
        }

        DoctorVisit saved = doctorVisitRepository.save(visit);
        return modelMapper.map(saved, DoctorVisitDTO.class);
    }

    @Override
    public Page<DoctorVisitDTO> getPatientVisits(Long patientId, int page, int size) {
        Long ownerId = getOwnerId();
        getPatient(patientId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("visitDate").descending());
        return doctorVisitRepository.findByPatientIdAndOwnerId(patientId, ownerId, pageable)
                .map(v -> {
                    DoctorVisitDTO dto = modelMapper.map(v, DoctorVisitDTO.class);
                    dto.setDoctorName(v.getDoctor().getName());
                    dto.setHasPrescription(v.getPrescription() != null);
                    if (v.getPrescription() != null) {
                        dto.setPrescriptionId(v.getPrescription().getId());
                    }
                    return dto;
                });
    }

    @Override
    public DoctorVisitDTO getVisitDetails(Long visitId) {
        Long ownerId = getOwnerId();
        DoctorVisit visit = doctorVisitRepository.findByIdAndOwnerId(visitId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit", "id", visitId));
        
        DoctorVisitDTO dto = modelMapper.map(visit, DoctorVisitDTO.class);
        dto.setDoctorName(visit.getDoctor().getName());
        return dto;
    }

    @Override
    @Transactional
    public PrescriptionDTO addPrescription(Long visitId, PrescriptionDTO prescriptionDTO) {
        Long ownerId = getOwnerId();
        DoctorVisit visit = doctorVisitRepository.findByIdAndOwnerId(visitId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit", "id", visitId));
        User owner = userRepository.findById(ownerId).orElseThrow();

        if (visit.getPrescription() != null) {
            throw new RuntimeException("Prescription already exists for this visit");
        }

        Prescription prescription = new Prescription();
        prescription.setVisit(visit);
        prescription.setOwner(owner);
        prescription.setPharmacistNotes(prescriptionDTO.getPharmacistNotes());
        
        List<Prescription.MedicationItem> medications = prescriptionDTO.getMedications().stream()
                .map(m -> modelMapper.map(m, Prescription.MedicationItem.class))
                .collect(Collectors.toList());
        prescription.setMedications(medications);

        Prescription saved = prescriptionRepository.save(prescription);
        return modelMapper.map(saved, PrescriptionDTO.class);
    }

    @Override
    public PrescriptionDTO getPrescriptionByVisit(Long visitId) {
        Long ownerId = getOwnerId();
        Prescription prescription = prescriptionRepository.findByVisitIdAndOwnerId(visitId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription", "visitId", visitId));
        
        PrescriptionDTO dto = modelMapper.map(prescription, PrescriptionDTO.class);
        dto.setDoctorName(prescription.getVisit().getDoctor().getName());
        dto.setVisitDate(prescription.getVisit().getVisitDate().toString());
        return dto;
    }

    @Override
    public List<PrescriptionDTO> getPrescriptionsByPatient(Long patientId) {
        Long ownerId = getOwnerId();
        getPatient(patientId);
        
        // Find all visits for patient, then mapped to prescriptions
        List<DoctorVisit> visits = doctorVisitRepository.findByPatientIdAndOwnerIdOrderByVisitDateDesc(patientId, ownerId);
        
        return visits.stream()
                .filter(v -> v.getPrescription() != null)
                .map(v -> {
                    Prescription p = v.getPrescription();
                    PrescriptionDTO dto = modelMapper.map(p, PrescriptionDTO.class);
                    dto.setDoctorName(v.getDoctor().getName());
                    dto.setVisitDate(v.getVisitDate().toString());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Visit Lifecycle Management
    
    @Override
    @Transactional
    public DoctorVisitDTO startVisit(Long visitId) {
        Long ownerId = getOwnerId();
        DoctorVisit visit = doctorVisitRepository.findByIdAndOwnerId(visitId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("DoctorVisit", "id", visitId));
        
        DoctorVisit updated = visitLifecycleService.startVisit(visitId);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public DoctorVisitDTO closeVisit(Long visitId) {
        Long ownerId = getOwnerId();
        DoctorVisit visit = doctorVisitRepository.findByIdAndOwnerId(visitId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("DoctorVisit", "id", visitId));
        
        String closedBy = CommonUtils.getLoggedInUser().getName();
        DoctorVisit updated = visitLifecycleService.closeVisit(visitId, closedBy);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public DoctorVisitDTO lockVisit(Long visitId) {
        Long ownerId = getOwnerId();
        DoctorVisit visit = doctorVisitRepository.findByIdAndOwnerId(visitId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("DoctorVisit", "id", visitId));
        
        String lockedBy = CommonUtils.getLoggedInUser().getName();
        DoctorVisit updated = visitLifecycleService.lockVisit(visitId, lockedBy);
        return mapToDTO(updated);
    }

    private DoctorVisitDTO mapToDTO(DoctorVisit visit) {
        DoctorVisitDTO dto = modelMapper.map(visit, DoctorVisitDTO.class);
        dto.setDoctorName(visit.getDoctor().getName());
        dto.setHasPrescription(visit.getPrescription() != null);
        if (visit.getPrescription() != null) {
            dto.setPrescriptionId(visit.getPrescription().getId());
        }
        return dto;
    }
}
