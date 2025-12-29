package com.vijay.User_Master.controller;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.dto.UserRequest;
import com.vijay.User_Master.dto.UserResponse;
import com.vijay.User_Master.dto.WorkerResponse;
import com.vijay.User_Master.dto.nursing.NurseScheduleDTO;
import com.vijay.User_Master.entity.NurseSchedule;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.entity.Ward;
import com.vijay.User_Master.entity.Worker;
import com.vijay.User_Master.repository.NurseScheduleRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.WardRepository;
import com.vijay.User_Master.repository.WorkerRepository;
import com.vijay.User_Master.service.AuthService;
import com.vijay.User_Master.service.WorkerUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/owner/nurses")
@RequiredArgsConstructor
public class NurseManagementController {

    private final WorkerRepository workerRepository;
    private final AuthService authService;
    private final WorkerUserService workerUserService;
    private final ModelMapper modelMapper;
    private final NurseScheduleRepository nurseScheduleRepository;
    private final WardRepository wardRepository;
    private final UserRepository userRepository;

    // --- READ-ONLY Nurse List (For Roster Assignment) ---

    @GetMapping
    public ResponseEntity<List<WorkerResponse>> getAllNurses() {
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<Worker> nurses = workerRepository.findAllNursesByOwner(ownerId, "ROLE_NURSE");
        List<WorkerResponse> response = nurses.stream()
                .map(worker -> modelMapper.map(worker, WorkerResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    // Note: CRUD operations (Create/Update/Delete) for Nurses are handled 
    // via the general "Staff Management" (Worker) module.

    // --- Shift Management (Roster) ---

    @GetMapping("/schedules")
    public ResponseEntity<List<NurseScheduleDTO>> getSchedules(@RequestParam LocalDate date) {
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        List<NurseSchedule> schedules = nurseScheduleRepository.findByOwnerIdAndScheduleDate(ownerId, date);
        
        List<NurseScheduleDTO> dtos = schedules.stream().map(s -> NurseScheduleDTO.builder()
                .id(s.getId())
                .nurseId(s.getNurse().getId())
                .nurseName(s.getNurse().getName())
                .wardId(s.getWard() != null ? s.getWard().getId() : null)
                .wardName(s.getWard() != null ? s.getWard().getName() : null)
                .scheduleDate(s.getScheduleDate())
                .shiftName(s.getShiftName())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .notes(s.getNotes())
                .build()
        ).collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/schedules")
    public ResponseEntity<?> createSchedule(@RequestBody NurseScheduleDTO dto) {
        CustomUserDetails userDetails = CommonUtils.getLoggedInUser();
        Long ownerId = userDetails.getId();
        
        // Security: Ensure owner exists
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new RuntimeException("Owner not found"));

        // Validate Nurse
        Worker nurse = workerRepository.findById(dto.getNurseId())
                .filter(w -> w.getUser().getId().equals(ownerId))
                .orElseThrow(() -> new RuntimeException("Nurse not found or access denied"));

        // Validate Ward (Optional)
        Ward ward = null;
        if (dto.getWardId() != null) {
            ward = wardRepository.findById(dto.getWardId())
                    .filter(w -> w.getOwner().getId().equals(ownerId)) // Assuming Ward has Owner check
                    .orElseThrow(() -> new RuntimeException("Ward not found"));
        }

        // Check Duplicates
        if (nurseScheduleRepository.existsByOwnerIdAndNurseIdAndScheduleDateAndShiftName(
                ownerId, dto.getNurseId(), dto.getScheduleDate(), dto.getShiftName())) {
            return ResponseEntity.badRequest().body("Nurse is already assigned to this shift on this date");
        }

        NurseSchedule schedule = NurseSchedule.builder()
                .nurse(nurse)
                .ward(ward)
                .scheduleDate(dto.getScheduleDate())
                .shiftName(dto.getShiftName())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .notes(dto.getNotes())
                .owner(owner)
                .build();

        nurseScheduleRepository.save(schedule);
        return ResponseEntity.ok("Shift Assigned Successfully");
    }

    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        Long ownerId = CommonUtils.getLoggedInUser().getId();
        NurseSchedule schedule = nurseScheduleRepository.findById(id)
                .filter(s -> s.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        
        nurseScheduleRepository.delete(schedule);
        return ResponseEntity.ok().build();
    }
}
