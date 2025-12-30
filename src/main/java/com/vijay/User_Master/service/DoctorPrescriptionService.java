package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.emr.PrescriptionDTO;
import java.util.List;

public interface DoctorPrescriptionService {
    List<PrescriptionDTO> getMyPrescriptions(Long userId);
    // Future: createPrescription
}
