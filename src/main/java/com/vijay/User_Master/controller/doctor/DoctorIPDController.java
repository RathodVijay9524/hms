package com.vijay.User_Master.controller.doctor;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.nursing.WardPatientDTO;
import com.vijay.User_Master.entity.DoctorProfile;
import com.vijay.User_Master.repository.DoctorProfileRepository;
import com.vijay.User_Master.service.NursingService;
import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctor/ipd")
@RequiredArgsConstructor
@Slf4j
public class DoctorIPDController {

    private final NursingService nursingService;
    private final DoctorProfileRepository doctorProfileRepository;
    private final com.vijay.User_Master.repository.PatientRepository patientRepository;
    private final com.vijay.User_Master.repository.WardPatientAssignmentRepository wardAssignmentRepository;

    @GetMapping("/my-patients")
    public ResponseEntity<?> getMyAdmittedPatients() {
        var loggedInUser = CommonUtils.getLoggedInUser();
        // log.info("Request to get assigned IPD patients for doctor user: {}", loggedInUser.getId());

        DoctorProfile doctor = doctorProfileRepository.findByUserIdAndOwnerId(loggedInUser.getId(), loggedInUser.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor Profile", "User ID", loggedInUser.getId()));

        List<WardPatientDTO> patients = nursingService.getAdmittedPatientsForDoctor(doctor.getId());
        return ExceptionUtil.createBuildResponse(patients, HttpStatus.OK);
    }

    @GetMapping("/wards")
    public ResponseEntity<?> getWards() {
        return ExceptionUtil.createBuildResponse(nursingService.getWards(), HttpStatus.OK);
    }

    @GetMapping("/available-beds")
    public ResponseEntity<?> getAvailableBeds(@org.springframework.web.bind.annotation.RequestParam Long wardId) {
        var loggedInUser = CommonUtils.getLoggedInUser();
        Long ownerId = loggedInUser.getOwnerId();

        // 1. Get Ward Details for Code prefix
        com.vijay.User_Master.entity.Ward ward = nursingService.getWardById(wardId); // Need to expose this or use repo
        // Since we don't have direct access to repo here easily without injecting, 
        // let's use the list we have or inject WardRepository if needed. 
        // Actually, let's just fetch all active assignments for this ward.
        
        List<com.vijay.User_Master.entity.WardPatientAssignment> activeAssignments = wardAssignmentRepository.findByWardIdAndOwnerIdAndIsDeletedFalseAndStatus(
                wardId, 
                ownerId, 
                com.vijay.User_Master.entity.WardPatientAssignment.AssignmentStatus.ACTIVE
        );
        
        java.util.Set<String> occupiedBeds = activeAssignments.stream()
                .map(com.vijay.User_Master.entity.WardPatientAssignment::getBedCode)
                .collect(java.util.stream.Collectors.toSet());

        // 2. Generate generic beds (e.g. WardCode-01 to WardCode-10)
        // We'll assume a standard capacity of 10 for now per ward for simplicity or dynamic
        List<String> availableBeds = new java.util.ArrayList<>();
        String prefix = ward.getCode();
        
        for (int i = 1; i <= 20; i++) {
            String bedCode = String.format("%s-%02d", prefix, i);
            if (!occupiedBeds.contains(bedCode)) {
                availableBeds.add(bedCode);
            }
        }
        
        return ExceptionUtil.createBuildResponse(availableBeds, HttpStatus.OK);
    }



    @GetMapping("/patient-status")
    public ResponseEntity<?> getPatientStatus(@org.springframework.web.bind.annotation.RequestParam String uhid) {
        var loggedInUser = CommonUtils.getLoggedInUser();
        Long ownerId = loggedInUser.getOwnerId();
        return ExceptionUtil.createBuildResponse(nursingService.getPatientAdmissionStatus(uhid, ownerId), HttpStatus.OK);
    }

    @org.springframework.web.bind.annotation.PostMapping("/admit")
    public ResponseEntity<?> admitPatient(@org.springframework.web.bind.annotation.RequestBody com.vijay.User_Master.dto.doctor.AdmitPatientRequest request) {
        var loggedInUser = CommonUtils.getLoggedInUser();
        DoctorProfile doctor = doctorProfileRepository.findByUserIdAndOwnerId(loggedInUser.getId(), loggedInUser.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor Profile", "User ID", loggedInUser.getId()));

        // Need patient ID from UHID
        com.vijay.User_Master.entity.Patient patient = patientRepository.findByUhidAndOwnerId(request.getUhid(), loggedInUser.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "UHID", request.getUhid()));

        com.vijay.User_Master.dto.nursing.AssignPatientRequestDTO assignRequest = new com.vijay.User_Master.dto.nursing.AssignPatientRequestDTO(
            request.getWardId(),
            patient.getId(),
            request.getBedCode(),
            doctor.getId()
        );

        return ExceptionUtil.createBuildResponse(nursingService.assignPatientToWard(assignRequest), HttpStatus.CREATED);
    }
}
