package com.vijay.User_Master.controller.doctor;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.dto.nursing.PatientLiteDTO;
import com.vijay.User_Master.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctor/patients")
@RequiredArgsConstructor
public class DoctorPatientController {

    private final PatientRepository patientRepository;

    @GetMapping("/list")
    public ResponseEntity<?> getAllPatients() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        
        // existing patients
        List<PatientLiteDTO> allPatients = patientRepository.findByOwnerId(ownerId).stream()
                .map(p -> {
                    Integer age = null;
                    if(p.getDateOfBirth() != null) {
                        age = java.time.Period.between(p.getDateOfBirth(), java.time.LocalDate.now()).getYears();
                    }
                    return new PatientLiteDTO(p.getId(), p.getName(), p.getUhid(), age);
                })
                .collect(Collectors.toList());

        // Return all patients (filtering/checking will happen on selection)
        return ExceptionUtil.createBuildResponse(allPatients, HttpStatus.OK);
    }
}
