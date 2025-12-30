package com.vijay.User_Master.controller.doctor;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.dto.DoctorConsultationDTO;
import com.vijay.User_Master.entity.DoctorProfile;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.DoctorProfileRepository;
import com.vijay.User_Master.service.InternalConsultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor/consults")
@RequiredArgsConstructor
@Slf4j
public class InternalConsultController {

    private final InternalConsultService consultService;
    private final DoctorProfileRepository doctorProfileRepository;

    private DoctorProfile getLoggedInDoctor() {
        var loggedInUser = CommonUtils.getLoggedInUser();
        return doctorProfileRepository.findByUserIdAndOwnerId(loggedInUser.getId(), loggedInUser.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor Profile", "User ID", loggedInUser.getId()));
    }

    @GetMapping("/incoming")
    public ResponseEntity<?> getIncoming() {
        DoctorProfile doctor = getLoggedInDoctor();
        List<DoctorConsultationDTO> consults = consultService.getIncomingConsults(doctor.getId());
        return ExceptionUtil.createBuildResponse(consults, HttpStatus.OK);
    }

    @GetMapping("/outgoing")
    public ResponseEntity<?> getOutgoing() {
        DoctorProfile doctor = getLoggedInDoctor();
        List<DoctorConsultationDTO> consults = consultService.getOutgoingConsults(doctor.getId());
        return ExceptionUtil.createBuildResponse(consults, HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        DoctorProfile doctor = getLoggedInDoctor();
        Map<String, Object> stats = consultService.getStats(doctor.getId());
        return ExceptionUtil.createBuildResponse(stats, HttpStatus.OK);
    }
}
