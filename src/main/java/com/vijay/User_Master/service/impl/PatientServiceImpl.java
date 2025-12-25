package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.lab.PatientDTO;
import com.vijay.User_Master.entity.Patient;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.PatientRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final com.vijay.User_Master.util.UHIDGenerator uhidGenerator;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public PatientDTO createPatient(PatientDTO patientDTO) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        Patient patient = modelMapper.map(patientDTO, Patient.class);
        patient.setOwner(owner);
        
        // Generate UHID if not provided
        if (patient.getUhid() == null || patient.getUhid().isEmpty()) {
            patient.setUhid(uhidGenerator.generate(ownerId));
        }

        Patient savedPatient = patientRepository.save(patient);
        return convertToDTO(savedPatient);
    }

    @Override
    public PatientDTO getPatientById(Long id) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        Patient patient = patientRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));
        return convertToDTO(patient);
    }

    @Override
    public Page<PatientDTO> getAllPatients(int page, int size) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        Pageable pageable = PageRequest.of(page, size);
        Page<Patient> patients = patientRepository.findByOwnerId(ownerId, pageable);
        return patients.map(this::convertToDTO);
    }

    @Override
    @Transactional
    public PatientDTO updatePatient(Long id, PatientDTO patientDTO) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        Patient patient = patientRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));

        patient.setName(patientDTO.getName());
        patient.setEmail(patientDTO.getEmail());
        patient.setPhone(patientDTO.getPhone());
        patient.setDateOfBirth(patientDTO.getDateOfBirth());
        patient.setGender(patientDTO.getGender());
        patient.setAddress(patientDTO.getAddress());

        Patient updatedPatient = patientRepository.save(patient);
        return convertToDTO(updatedPatient);
    }

    @Override
    @Transactional
    public void deletePatient(Long id) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        Patient patient = patientRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));
        patientRepository.delete(patient);
    }

    @Override
    public long getPatientCount() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return patientRepository.countByOwnerId(ownerId);
    }



    @Override
    public PatientDTO findByUhid(String uhid) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        Patient patient = patientRepository.findByUhidAndOwnerId(uhid, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "uhid", uhid));
        return convertToDTO(patient);
    }

    private PatientDTO convertToDTO(Patient patient) {
        PatientDTO dto = modelMapper.map(patient, PatientDTO.class);
        if (patient.getDateOfBirth() != null) {
            dto.setAge(java.time.Period.between(patient.getDateOfBirth(), java.time.LocalDate.now()).getYears());
        }
        return dto;
    }
}
