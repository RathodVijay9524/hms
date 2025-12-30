package com.vijay.User_Master.controller.doctor;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.Helper.ExceptionUtil;
import com.vijay.User_Master.dto.emr.PrescriptionDTO;
import com.vijay.User_Master.service.DoctorPrescriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/doctor/prescriptions")
@RequiredArgsConstructor
@Slf4j
public class DoctorPrescriptionController {

    private final DoctorPrescriptionService prescriptionService;

    @GetMapping("/my-list")
    public ResponseEntity<?> getMyPrescriptions() {
        Long userId = CommonUtils.getLoggedInUser().getId();
        List<PrescriptionDTO> prescriptions = prescriptionService.getMyPrescriptions(userId);
        return ExceptionUtil.createBuildResponse(prescriptions, HttpStatus.OK);
    }
}
