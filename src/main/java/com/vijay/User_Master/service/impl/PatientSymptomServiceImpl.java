package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.emr.PatientSymptomDTO;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.PatientSymptomService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for patient symptom management
 */
@Service
@RequiredArgsConstructor
public class PatientSymptomServiceImpl implements PatientSymptomService {
    
    private final PatientSymptomRepository symptomRepository;
    private final SymptomMasterRepository symptomMasterRepository;
    private final DoctorVisitRepository visitRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    
    @Override
    @Transactional
    public PatientSymptomDTO addSymptom(Long visitId, PatientSymptomDTO symptomDTO) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        
        DoctorVisit visit = visitRepository.findByIdAndOwnerId(visitId, ownerId)
            .orElseThrow(() -> new RuntimeException("Visit not found"));
        
        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        PatientSymptom symptom = PatientSymptom.builder()
            .visit(visit)
            .symptomName(symptomDTO.getSymptomName())
            .severity(symptomDTO.getSeverity())
            .duration(symptomDTO.getDuration())
            .notes(symptomDTO.getNotes())
            .owner(owner)
            .build();
        
        PatientSymptom saved = symptomRepository.save(symptom);
        return modelMapper.map(saved, PatientSymptomDTO.class);
    }
    
    @Override
    public List<PatientSymptomDTO> getSymptomsByVisit(Long visitId) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        
        return symptomRepository.findByVisitIdAndOwnerIdOrderByCreatedOnDesc(visitId, ownerId)
            .stream()
            .map(symptom -> modelMapper.map(symptom, PatientSymptomDTO.class))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<PatientSymptomDTO> getSymptomsByPatient(Long patientId) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        
        return symptomRepository.findByVisitPatientIdAndOwnerIdOrderByCreatedOnDesc(patientId, ownerId)
            .stream()
            .map(symptom -> modelMapper.map(symptom, PatientSymptomDTO.class))
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteSymptom(Long symptomId) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        
        PatientSymptom symptom = symptomRepository.findById(symptomId)
            .orElseThrow(() -> new RuntimeException("Symptom not found"));
        
        if (!symptom.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        symptomRepository.delete(symptom);
    }
    
    @Override
    public List<SymptomMaster> getAllCommonSymptoms() {
        return symptomMasterRepository.findAll();
    }
    
    @Override
    public List<SymptomMaster> searchSymptoms(String query) {
        return symptomMasterRepository.findByNameContainingIgnoreCase(query);
    }
}
