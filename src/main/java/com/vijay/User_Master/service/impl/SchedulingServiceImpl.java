package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.DoctorScheduleRequest;
import com.vijay.User_Master.dto.DoctorScheduleResponse;
import com.vijay.User_Master.dto.TimeSlotResponse;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.DoctorProfileRepository;
import com.vijay.User_Master.repository.DoctorScheduleRepository;
import com.vijay.User_Master.repository.TimeSlotRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.service.SchedulingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class SchedulingServiceImpl implements SchedulingService {

    private final DoctorScheduleRepository doctorScheduleRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public DoctorScheduleResponse saveSchedule(DoctorScheduleRequest request) {
        log.info("Saving schedule for doctor ID: {} on {}", request.getDoctorId(), request.getDayOfWeek());
        
        DoctorProfile doctor = doctorProfileRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "ID", request.getDoctorId()));

        var loggedInUser = CommonUtils.getLoggedInUser();
        User owner = userRepository.findById(loggedInUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "ID", loggedInUser.getId()));

        DoctorSchedule schedule = mapper.map(request, DoctorSchedule.class);
        schedule.setDoctor(doctor);
        schedule.setOwner(owner);

        DoctorSchedule saved = doctorScheduleRepository.save(schedule);
        return convertToScheduleResponse(saved);
    }

    @Override
    public List<DoctorScheduleResponse> getDoctorSchedules(Long doctorId) {
        return doctorScheduleRepository.findByDoctorIdAndActiveTrue(doctorId)
                .stream()
                .map(this::convertToScheduleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<TimeSlotResponse> getAvailableSlots(Long doctorId, LocalDate date) {
        // Ensure slots are generated
        generateSlotsForDate(doctorId, date);
        
        return timeSlotRepository.findByDoctorIdAndDateAndStatus(doctorId, date, TimeSlot.SlotStatus.AVAILABLE)
                .stream()
                .map(this::convertToTimeSlotResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void generateSlotsForDate(Long doctorId, LocalDate date) {
        if (timeSlotRepository.existsByDoctorIdAndDate(doctorId, date)) {
            log.info("Slots already exist for doctor ID: {} on {}", doctorId, date);
            return;
        }

        // Try to find a schedule for this specific date first
        List<DoctorSchedule> schedules = doctorScheduleRepository.findByDoctorIdAndSpecificDateAndActiveTrue(doctorId, date);
        
        // If no specific date schedule, fall back to weekly schedule
        if (schedules.isEmpty()) {
            String dayName = date.getDayOfWeek().name();
            DoctorSchedule.DayOfWeek dayOfWeek = DoctorSchedule.DayOfWeek.valueOf(dayName);
            schedules = doctorScheduleRepository.findByDoctorIdAndDayOfWeekAndSpecificDateIsNullAndActiveTrue(doctorId, dayOfWeek);
            log.info("Using weekly schedule for {} (Day: {})", date, dayOfWeek);
        } else {
            log.info("Using specific date schedule for {}", date);
        }

        if (schedules.isEmpty()) {
            log.warn("No active schedule found for doctor ID: {} on {}", doctorId, date);
            return;
        }

        List<TimeSlot> slotsToSave = new ArrayList<>();
        User owner = schedules.get(0).getOwner();
        DoctorProfile doctor = schedules.get(0).getDoctor();

        for (DoctorSchedule schedule : schedules) {
            log.info("Generating slots for schedule ID: {} ({} to {} with {} min duration)", 
                schedule.getId(), schedule.getStartTime(), schedule.getEndTime(), schedule.getSlotDuration());
            
            LocalTime current = schedule.getStartTime();
            LocalTime endTime = schedule.getEndTime();
            int duration = schedule.getSlotDuration();

            if (endTime.isBefore(current)) {
                log.error("Invalid schedule: End time {} is before Start time {}", endTime, current);
                continue;
            }

            while (current.plusMinutes(duration).isBefore(endTime) || 
                   current.plusMinutes(duration).equals(endTime)) {
                
                TimeSlot slot = TimeSlot.builder()
                        .doctor(doctor)
                        .date(date)
                        .startTime(current)
                        .endTime(current.plusMinutes(duration))
                        .status(TimeSlot.SlotStatus.AVAILABLE)
                        .owner(owner)
                        .build();
                
                slotsToSave.add(slot);
                current = current.plusMinutes(duration);
            }
            log.info("Total slots generated for this schedule: {}", slotsToSave.size());
        }

        if (!slotsToSave.isEmpty()) {
            timeSlotRepository.saveAll(slotsToSave);
            log.info("Generated {} slots for doctor ID: {} on {}", slotsToSave.size(), doctorId, date);
        }
    }

    @Override
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        doctorScheduleRepository.deleteById(scheduleId);
    }

    private DoctorScheduleResponse convertToScheduleResponse(DoctorSchedule schedule) {
        DoctorScheduleResponse response = mapper.map(schedule, DoctorScheduleResponse.class);
        response.setDoctorId(schedule.getDoctor().getId());
        response.setDoctorName(schedule.getDoctor().getUser().getName());
        return response;
    }

    private TimeSlotResponse convertToTimeSlotResponse(TimeSlot slot) {
        TimeSlotResponse response = mapper.map(slot, TimeSlotResponse.class);
        response.setDoctorId(slot.getDoctor().getId());
        response.setDoctorName(slot.getDoctor().getUser().getName());
        response.setAvailable(slot.getStatus() == TimeSlot.SlotStatus.AVAILABLE);
        return response;
    }
}
