package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByDoctorIdAndDate(Long doctorId, LocalDate date);
    List<TimeSlot> findByDoctorIdAndDateAndStatus(Long doctorId, LocalDate date, TimeSlot.SlotStatus status);
    boolean existsByDoctorIdAndDate(Long doctorId, LocalDate date);
    void deleteByDoctorIdAndDate(Long doctorId, LocalDate date);
}
