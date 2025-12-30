package com.vijay.User_Master.service.impl;

import com.vijay.User_Master.dto.lab.PatientDTO;
import com.vijay.User_Master.dto.reception.*;
import com.vijay.User_Master.entity.*;
import com.vijay.User_Master.exceptions.ResourceNotFoundException;
import com.vijay.User_Master.repository.*;
import com.vijay.User_Master.service.PatientService;
import com.vijay.User_Master.service.ReceptionService;
import com.vijay.User_Master.Helper.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReceptionServiceImpl implements ReceptionService {

    private final EnquiryRepository enquiryRepository;
    private final VisitorLogRepository visitorLogRepository;
    private final QueueTokenRepository queueTokenRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final PatientService patientService;
    private final ModelMapper modelMapper;

    @Override
    public ReceptionStatsDTO getDashboardStats() {
        return getStatsByDate(LocalDate.now());
    }

    @Override
    public ReceptionStatsDTO getStatsByDate(LocalDate date) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        
        long todayReg = patientRepository.countByOwnerId(ownerId); // Simple count for now
        long pendingTokens = queueTokenRepository.findByOwnerIdAndTokenDate(ownerId, date).stream()
                .filter(t -> t.getStatus() == QueueToken.TokenStatus.WAITING).count();
        
        List<Appointment> appts = appointmentRepository.findByAppointmentDateAndOwnerId(date, ownerId);
        long appointments = appts.size();
        long checkedIn = appts.stream().filter(a -> a.getStatus() == Appointment.AppointmentStatus.CHECKED_IN).count();
        long noShow = appts.stream().filter(a -> a.getStatus() == Appointment.AppointmentStatus.NO_SHOW).count();

        long inPremise = visitorLogRepository.findByOwnerId(ownerId).stream()
                .filter(v -> v.getCheckOutTime() == null).count();

        return ReceptionStatsDTO.builder()
                .todayRegistrations(todayReg)
                .pendingTokens(pendingTokens)
                .totalAppointments(appointments)
                .expectedTodayCount(appointments)
                .checkedInCount(checkedIn)
                .noShowCount(noShow)
                .inPremiseVisitors(inPremise)
                .avgWaitTime("15 min") // Mocked for now
                .build();
    }

    @Override
    @Transactional
    public EnquiryDTO createEnquiry(EnquiryDTO enquiryDTO) {
        User owner = User.builder().id(CommonUtils.getLoggedInUser().getOwnerId()).build();
        Enquiry enquiry = modelMapper.map(enquiryDTO, Enquiry.class);
        enquiry.setOwner(owner);
        enquiry.setStatus(Enquiry.EnquiryStatus.PENDING);
        Enquiry saved = enquiryRepository.save(enquiry);
        return modelMapper.map(saved, EnquiryDTO.class);
    }

    @Override
    public List<EnquiryDTO> getAllEnquiries() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return enquiryRepository.findByOwnerId(ownerId).stream()
                .map(e -> modelMapper.map(e, EnquiryDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EnquiryDTO updateEnquiryStatus(Long id, String status, String notes) {
        Enquiry enquiry = enquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enquiry", "id", id));
        enquiry.setStatus(Enquiry.EnquiryStatus.valueOf(status));
        enquiry.setResolutionNotes(notes);
        return modelMapper.map(enquiryRepository.save(enquiry), EnquiryDTO.class);
    }

    @Override
    @Transactional
    public VisitorLogDTO checkInVisitor(VisitorLogDTO visitorDTO) {
        User owner = User.builder().id(CommonUtils.getLoggedInUser().getOwnerId()).build();
        VisitorLog visitor = modelMapper.map(visitorDTO, VisitorLog.class);
        visitor.setOwner(owner);
        visitor.setCheckInTime(LocalDateTime.now());
        VisitorLog saved = visitorLogRepository.save(visitor);
        return modelMapper.map(saved, VisitorLogDTO.class);
    }

    @Override
    @Transactional
    public VisitorLogDTO checkOutVisitor(Long id) {
        VisitorLog visitor = visitorLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VisitorLog", "id", id));
        visitor.setCheckOutTime(LocalDateTime.now());
        return modelMapper.map(visitorLogRepository.save(visitor), VisitorLogDTO.class);
    }

    @Override
    public List<VisitorLogDTO> getAllVisitorLogs() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return visitorLogRepository.findByOwnerId(ownerId).stream()
                .map(v -> modelMapper.map(v, VisitorLogDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QueueTokenDTO issueToken(QueueTokenDTO tokenDTO) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        User owner = User.builder().id(ownerId).build();
        
        long count = queueTokenRepository.countByOwnerIdAndTokenDate(ownerId, LocalDate.now());
        String tokenNumber = "T-" + (100 + count + 1);

        QueueToken token = QueueToken.builder()
                .tokenNumber(tokenNumber)
                .patientName(tokenDTO.getPatientName())
                .phone(tokenDTO.getPhone())
                .tokenDate(LocalDate.now())
                .status(QueueToken.TokenStatus.WAITING)
                .owner(owner)
                .build();

        if (tokenDTO.getPatientId() != null) {
            token.setPatient(patientRepository.findById(tokenDTO.getPatientId()).orElse(null));
        }

        if (tokenDTO.getDepartmentId() != null) {
            token.setDepartment(departmentRepository.findById(tokenDTO.getDepartmentId()).orElse(null));
        }

        if (tokenDTO.getDoctorId() != null) {
            token.setDoctor(doctorProfileRepository.findById(tokenDTO.getDoctorId()).orElse(null));
        }

        QueueToken saved = queueTokenRepository.save(token);
        return modelMapper.map(saved, QueueTokenDTO.class);
    }

    @Override
    public List<QueueTokenDTO> getTodayTokens() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return queueTokenRepository.findByOwnerIdAndTokenDate(ownerId, LocalDate.now()).stream()
                .map(t -> {
                    QueueTokenDTO dto = modelMapper.map(t, QueueTokenDTO.class);
                    if (t.getDepartment() != null) dto.setDepartmentName(t.getDepartment().getName());
                    if (t.getDoctor() != null && t.getDoctor().getUser() != null) dto.setDoctorName(t.getDoctor().getUser().getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QueueTokenDTO updateTokenStatus(Long id, String status) {
        QueueToken token = queueTokenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QueueToken", "id", id));
        token.setStatus(QueueToken.TokenStatus.valueOf(status));
        return modelMapper.map(queueTokenRepository.save(token), QueueTokenDTO.class);
    }

    @Override
    public Object searchPatients(String query) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return patientRepository.findByOwnerId(ownerId).stream()
                .filter(p -> p.getName().toLowerCase().contains(query.toLowerCase()) || 
                             (p.getUhid() != null && p.getUhid().contains(query)) ||
                             (p.getPhone() != null && p.getPhone().contains(query)))
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public List<PatientDTO> getAllPatients() {
        return patientService.getAllPatients();
    }

    @Override
    public List<com.vijay.User_Master.dto.AppointmentResponse> getTodayAppointments() {
        return getAppointmentsByDate(LocalDate.now());
    }

    @Override
    public List<com.vijay.User_Master.dto.AppointmentResponse> getAppointmentsByDate(LocalDate date) {
        Long ownerId = com.vijay.User_Master.Helper.CommonUtils.getLoggedInUser().getOwnerId();
        return appointmentRepository.findByAppointmentDateAndOwnerId(date, ownerId).stream()
                .map(this::convertToAppointmentResponse)
                .collect(Collectors.toList());
    }

    private com.vijay.User_Master.dto.AppointmentResponse convertToAppointmentResponse(Appointment appointment) {
        com.vijay.User_Master.dto.AppointmentResponse response = modelMapper.map(appointment, com.vijay.User_Master.dto.AppointmentResponse.class);
        response.setPatientId(appointment.getPatient().getId());
        response.setPatientName(appointment.getPatient().getName());
        response.setPatientUhid(appointment.getPatient().getUhid());
        response.setDoctorId(appointment.getDoctor().getId());
        response.setDoctorName(appointment.getDoctor().getUser().getName());
        if (appointment.getDoctor().getDepartment() != null) {
            response.setDepartmentName(appointment.getDoctor().getDepartment().getName());
        }
        if (appointment.getSlot() != null) {
            response.setSlotId(appointment.getSlot().getId());
        }
        if (appointment.getVisit() != null) {
            response.setVisitId(appointment.getVisit().getId());
        }
        return response;
    }


    @Override
    @Transactional(readOnly = true)
    public WeeklyScheduleGridDTO getWeeklySchedule(LocalDate startDate) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        
        // Calculate start of week (assuming Monday start)
        LocalDate startOfWeek = startDate;
        while (startOfWeek.getDayOfWeek() != java.time.DayOfWeek.MONDAY) {
            startOfWeek = startOfWeek.minusDays(1);
        }
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // Fetch all active doctors
        List<DoctorProfile> doctors = doctorProfileRepository.findByOwnerIdAndStatusTrue(ownerId).stream()
                .filter(d -> d.getUser() != null && Boolean.TRUE.equals(d.getStatus()))
                .collect(Collectors.toList());

        List<DoctorWeeklyScheduleDTO> doctorSchedules = new java.util.ArrayList<>();

        for (DoctorProfile doc : doctors) {
            DoctorWeeklyScheduleDTO docDto = DoctorWeeklyScheduleDTO.builder()
                    .doctorId(doc.getId())
                    .doctorName(doc.getUser().getName())
                    .department(doc.getDepartment() != null ? doc.getDepartment().getName() : "General")
                    .weeklyShifts(new java.util.HashMap<>())
                    .build();

            // For each day of the week
            for (LocalDate date = startOfWeek; !date.isAfter(endOfWeek); date = date.plusDays(1)) {
                List<String> formattedShifts = new java.util.ArrayList<>();
                
                // 1. Check for specific date schedule
                List<DoctorSchedule> specificSchedules = doctorScheduleRepository.findByDoctorIdAndSpecificDateAndActiveTrue(doc.getId(), date);
                
                List<DoctorSchedule> activeSchedulesForDay;
                if (!specificSchedules.isEmpty()) {
                    activeSchedulesForDay = specificSchedules;
                } else {
                    // 2. Fallback to weekly schedule
                    activeSchedulesForDay = doctorScheduleRepository.findByDoctorIdAndDayOfWeekAndSpecificDateIsNullAndActiveTrue(
                            doc.getId(), DoctorSchedule.DayOfWeek.valueOf(date.getDayOfWeek().name()));
                }

                for (DoctorSchedule s : activeSchedulesForDay) {
                    String fmt = formatTime(s.getStartTime()) + " - " + formatTime(s.getEndTime());
                    formattedShifts.add(fmt);
                }

                if (formattedShifts.isEmpty()) {
                    formattedShifts.add("OFF DUTY");
                }
                
                docDto.getWeeklyShifts().put(date.toString(), formattedShifts);
            }
            doctorSchedules.add(docDto);
        }

        return WeeklyScheduleGridDTO.builder()
                .startDate(startOfWeek)
                .endDate(endOfWeek)
                .doctorSchedules(doctorSchedules)
                .build();
    }

    private String formatTime(java.time.LocalTime time) {
        if (time == null) return "";
        return time.format(java.time.format.DateTimeFormatter.ofPattern("hh a"));
    }
}
