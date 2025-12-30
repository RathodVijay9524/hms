package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.DoctorConsultationDTO;
import java.util.List;
import java.util.Map;

public interface InternalConsultService {
    List<DoctorConsultationDTO> getIncomingConsults(Long doctorId);
    List<DoctorConsultationDTO> getOutgoingConsults(Long doctorId);
    // placeholder for create/update logic if needed via controller
    Map<String, Object> getStats(Long doctorId);
}
