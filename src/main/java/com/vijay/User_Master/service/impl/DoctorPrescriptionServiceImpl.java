package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.emr.PrescriptionDTO;
import com.vijay.User_Master.entity.Prescription;
import com.vijay.User_Master.repository.PrescriptionRepository;
import com.vijay.User_Master.service.DoctorPrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorPrescriptionServiceImpl implements DoctorPrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    private Long getOwnerId() {
        return CommonUtils.getLoggedInUser().getOwnerId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionDTO> getMyPrescriptions(Long userId) {
        // userId is the Doctor's User ID.
        Long ownerId = getOwnerId();
        return prescriptionRepository.findByVisitDoctorIdAndOwnerIdOrderByCreatedOnDesc(userId, ownerId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private PrescriptionDTO toDTO(Prescription p) {
        return PrescriptionDTO.builder()
                .id(p.getId())
                .visitId(p.getVisit().getId())
                .visitDate(p.getVisit().getVisitDate() != null ? p.getVisit().getVisitDate().toLocalDate().toString() : "N/A")
                .doctorName(p.getVisit().getDoctorName())
                .pharmacistNotes(p.getPharmacistNotes())
                .medications(p.getMedications().stream().map(m -> 
                        PrescriptionDTO.MedicationItemDTO.builder()
                                .medicineName(m.getMedicineName())
                                .dosage(m.getDosage())
                                .frequency(m.getFrequency())
                                .duration(m.getDuration())
                                .quantity(m.getQuantity())
                                .instructions(m.getInstructions())
                                .build()
                ).collect(Collectors.toList()))
                .build();
    }
}
