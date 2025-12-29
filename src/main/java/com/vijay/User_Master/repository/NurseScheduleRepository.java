package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.NurseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NurseScheduleRepository extends JpaRepository<NurseSchedule, Long> {

    List<NurseSchedule> findByOwnerIdAndScheduleDate(Long ownerId, LocalDate date);

    List<NurseSchedule> findByOwnerIdAndScheduleDateAndShiftName(Long ownerId, LocalDate date, String shiftName);

    List<NurseSchedule> findByOwnerIdAndNurseIdAndScheduleDateBetween(Long ownerId, Long nurseId, LocalDate start, LocalDate end);
    
    // Check for overlap or duplicate assignment
    boolean existsByOwnerIdAndNurseIdAndScheduleDateAndShiftName(Long ownerId, Long nurseId, LocalDate date, String shiftName);
}
