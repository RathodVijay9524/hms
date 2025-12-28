package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.emr.DoctorProfileDTO;
import com.vijay.User_Master.entity.Department;
import com.vijay.User_Master.entity.DoctorProfile;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.DepartmentRepository;
import com.vijay.User_Master.repository.DoctorProfileRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.DoctorProfileService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorProfileServiceImpl implements DoctorProfileService {

    private final DoctorProfileRepository doctorProfileRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;

    @Override
    public DoctorProfileDTO createOrUpdateDoctorProfile(DoctorProfileDTO dto) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        User owner = userRepository.findById(ownerId).orElseThrow();
        
        User doctorUser = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isDoctor = doctorUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ROLE_DOCTOR"));
        if (!isDoctor) {
            throw new RuntimeException("User '" + doctorUser.getUsername() + "' does not have ROLE_DOCTOR and cannot be linked as a doctor.");
        }

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .filter(d -> d.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new RuntimeException("Department not found or access denied"));

        DoctorProfile profile = doctorProfileRepository.findByUserIdAndOwnerId(dto.getUserId(), ownerId)
                .orElse(new DoctorProfile());

        profile.setUser(doctorUser);
        profile.setDepartment(department);
        profile.setSpecialization(dto.getSpecialization());
        profile.setQualification(dto.getQualification());
        profile.setRegistrationNumber(dto.getRegistrationNumber());
        profile.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
        profile.setOwner(owner);

        DoctorProfile saved = doctorProfileRepository.save(profile);
        DoctorProfileDTO result = modelMapper.map(saved, DoctorProfileDTO.class);
        result.setDoctorName(doctorUser.getName());
        result.setUserId(doctorUser.getId());
        result.setDepartmentName(department.getName());
        result.setDepartmentId(department.getId());
        return result;
    }

    @Override
    public List<DoctorProfileDTO> getAllDoctorProfiles() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return doctorProfileRepository.findByOwnerId(ownerId).stream()
                .map(p -> {
                    DoctorProfileDTO dto = modelMapper.map(p, DoctorProfileDTO.class);
                    dto.setDoctorName(p.getUser().getName());
                    dto.setDepartmentName(p.getDepartment().getName());
                    dto.setUserId(p.getUser().getId());
                    dto.setDepartmentId(p.getDepartment().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public DoctorProfileDTO getDoctorProfileByUserId(Long userId) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        DoctorProfile profile = doctorProfileRepository.findByUserIdAndOwnerId(userId, ownerId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
        
        DoctorProfileDTO dto = modelMapper.map(profile, DoctorProfileDTO.class);
        dto.setDoctorName(profile.getUser().getName());
        dto.setUserId(profile.getUser().getId());
        dto.setDepartmentName(profile.getDepartment().getName());
        dto.setDepartmentId(profile.getDepartment().getId());
        return dto;
    }

    @Override
    public void deleteDoctorProfile(Long id) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        DoctorProfile profile = doctorProfileRepository.findById(id)
                .filter(p -> p.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new RuntimeException("Profile not found or access denied"));
        doctorProfileRepository.delete(profile);
    }
}
