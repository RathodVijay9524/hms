package com.vijay.User_Master.service;

import com.vijay.User_Master.dto.emr.DoctorProfileDTO;
import java.util.List;

public interface DoctorProfileService {
    DoctorProfileDTO createOrUpdateDoctorProfile(DoctorProfileDTO doctorProfileDTO);
    List<DoctorProfileDTO> getAllDoctorProfiles();
    DoctorProfileDTO getDoctorProfileByUserId(Long userId);
    void deleteDoctorProfile(Long id);
}
