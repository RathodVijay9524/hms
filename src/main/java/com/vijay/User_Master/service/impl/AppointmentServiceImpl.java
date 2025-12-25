package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.AppointmentRequest;
import com.vijay.User_Master.dto.AppointmentResponse;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.exceptions.BadApiRequestException;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.AppointmentService;
import com.vijay.User_Master.service.PatientEMRService;
import com.vijay.User_Master.service.BillingService;
import com.vijay.User_Master.dto.emr.DoctorVisitDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final PatientEMRService patientEMRService;
    private final DoctorVisitRepository doctorVisitRepository;
    private final BillingService billingService;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public AppointmentResponse bookAppointment(AppointmentRequest request) {
        log.info("Booking appointment for patient ID: {} with doctor ID: {}", request.getPatientId(), request.getDoctorId());

        var loggedInUser = CommonUtils.getLoggedInUser();
        User owner = userRepository.findById(loggedInUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "ID", loggedInUser.getId()));

        Patient patient = patientRepository.findByIdAndOwnerId(request.getPatientId(), owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "ID", request.getPatientId()));

        DoctorProfile doctor = doctorProfileRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "ID", request.getDoctorId()));

        TimeSlot slot = timeSlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("TimeSlot", "ID", request.getSlotId()));

        if (slot.getStatus() != TimeSlot.SlotStatus.AVAILABLE) {
            throw new BadApiRequestException("Time slot is no longer available.");
        }

        // Generate Appointment Number: APT-YYYYMMDD-SEQ
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = appointmentRepository.countByAppointmentDate(LocalDate.now()) + 1;
        String appointmentNumber = String.format("APT-%s-%03d", dateStr, count);

        Appointment appointment = Appointment.builder()
                .appointmentNumber(appointmentNumber)
                .patient(patient)
                .doctor(doctor)
                .slot(slot)
                .appointmentDate(slot.getDate())
                .appointmentTime(slot.getStartTime())
                .status(Appointment.AppointmentStatus.BOOKED)
                .reasonForVisit(request.getReasonForVisit())
                .notes(request.getNotes())
                .owner(owner)
                .build();

        // Update slot status
        slot.setStatus(TimeSlot.SlotStatus.BOOKED);
        timeSlotRepository.save(slot);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment booked successfully with number: {}", appointmentNumber);

        return convertToResponse(savedAppointment);
    }

    @Override
    @Transactional
    public AppointmentResponse updateStatus(Long id, Appointment.AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "ID", id));

        appointment.setStatus(status);
        
        // If cancelled, release the slot
        if (status == Appointment.AppointmentStatus.CANCELLED) {
            TimeSlot slot = appointment.getSlot();
            slot.setStatus(TimeSlot.SlotStatus.AVAILABLE);
            timeSlotRepository.save(slot);
        }

        Appointment updated = appointmentRepository.save(appointment);

        // Auto-create EMR Visit if status is CHECKED_IN
        if (status == Appointment.AppointmentStatus.CHECKED_IN && updated.getVisit() == null) {
            log.info("Auto-creating EMR visit for appointment: {}", updated.getAppointmentNumber());
            
            DoctorVisitDTO visitDTO = DoctorVisitDTO.builder()
                    .patientId(updated.getPatient().getId())
                    .doctorId(updated.getDoctor().getUser().getId())
                    .visitType(DoctorVisit.VisitType.OPD)
                    .visitDate(java.time.LocalDateTime.now())
                    .status(VisitStatus.CREATED)
                    .symptoms(updated.getReasonForVisit())
                    .build();

            DoctorVisitDTO createdVisit = patientEMRService.createVisit(updated.getPatient().getId(), visitDTO);
            
            // Link visit back to appointment
            DoctorVisit visitEntity = doctorVisitRepository.findById(createdVisit.getId()).orElseThrow();
            updated.setVisit(visitEntity);
            appointmentRepository.save(updated);
            
            // Bidirectional link (DoctorVisit is already saved by createVisit, we just need to update it with the appointment)
            visitEntity.setAppointment(updated);
            doctorVisitRepository.save(visitEntity);

            // Trigger Auto-Billing
            try {
                log.info("Auto-triggering billing for appointment: {}", updated.getAppointmentNumber());
                billingService.generateBillFromAppointment(updated.getId());
            } catch (Exception e) {
                log.error("Failed to auto-generate bill: {}", e.getMessage());
                // Don't fail the whole transaction if billing fails (optional strategy)
            }
        }

        return convertToResponse(updated);
    }

    @Override
    public List<AppointmentResponse> getTodayAppointments() {
        var loggedInUser = CommonUtils.getLoggedInUser();
        return appointmentRepository.findByAppointmentDateAndOwnerId(LocalDate.now(), loggedInUser.getId())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "ID", id));
        return convertToResponse(appointment);
    }

    private AppointmentResponse convertToResponse(Appointment appointment) {
        AppointmentResponse response = mapper.map(appointment, AppointmentResponse.class);
        response.setPatientName(appointment.getPatient().getName());
        response.setPatientUhid(appointment.getPatient().getUhid());
        response.setDoctorName(appointment.getDoctor().getUser().getName());
        response.setSlotId(appointment.getSlot().getId());
        if (appointment.getVisit() != null) {
            response.setVisitId(appointment.getVisit().getId());
        }
        return response;
    }
}
