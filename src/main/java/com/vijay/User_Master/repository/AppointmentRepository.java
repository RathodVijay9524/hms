package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByOwnerId(Long ownerId);
    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate date);
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByAppointmentDateAndOwnerId(LocalDate date, Long ownerId);
    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);
    long countByAppointmentDate(LocalDate date);
    long countByAppointmentDateAndOwnerId(LocalDate date, Long ownerId);
}
