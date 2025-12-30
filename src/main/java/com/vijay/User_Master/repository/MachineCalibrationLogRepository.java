package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.MachineCalibrationLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineCalibrationLogRepository extends JpaRepository<MachineCalibrationLog, Long> {

    List<MachineCalibrationLog> findByOwnerIdAndIsDeletedFalseOrderByCalibrationDateDesc(Long ownerId, Pageable pageable);

    List<MachineCalibrationLog> findByMachineIdAndOwnerIdAndIsDeletedFalseOrderByCalibrationDateDesc(Long machineId, Long ownerId);

    List<MachineCalibrationLog> findByOwnerIdAndIsDeletedFalseOrderByCalibrationDateDesc(Long ownerId);
}
