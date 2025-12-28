package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.DoctorSchedule;
import com.vijay.User_Master.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    List<DoctorSchedule> findByDoctorIdAndActiveTrue(Long doctorId);
    List<DoctorSchedule> findByOwnerId(Long ownerId);
    List<DoctorSchedule> findByDoctorIdAndDayOfWeekAndActiveTrue(Long doctorId, DoctorSchedule.DayOfWeek dayOfWeek);
    List<DoctorSchedule> findByDoctorIdAndSpecificDateAndActiveTrue(Long doctorId, java.time.LocalDate specificDate);
    List<DoctorSchedule> findByDoctorIdAndDayOfWeekAndSpecificDateIsNullAndActiveTrue(Long doctorId, DoctorSchedule.DayOfWeek dayOfWeek);
}
