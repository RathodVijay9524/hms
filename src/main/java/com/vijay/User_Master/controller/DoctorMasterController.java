package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.emr.DoctorProfileDTO;
import com.vijay.User_Master.service.DoctorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/masters/doctors")
@RequiredArgsConstructor
public class DoctorMasterController {

    private final DoctorProfileService doctorProfileService;

    @PostMapping
    public ResponseEntity<DoctorProfileDTO> createOrUpdateProfile(@RequestBody DoctorProfileDTO dto) {
        return ResponseEntity.ok(doctorProfileService.createOrUpdateDoctorProfile(dto));
    }

    @GetMapping
    public ResponseEntity<List<DoctorProfileDTO>> getAllProfiles() {
        return ResponseEntity.ok(doctorProfileService.getAllDoctorProfiles());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<DoctorProfileDTO> getProfileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(doctorProfileService.getDoctorProfileByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        doctorProfileService.deleteDoctorProfile(id);
        return ResponseEntity.noContent().build();
    }
}
