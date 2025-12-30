package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.LabMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabMachineRepository extends JpaRepository<LabMachine, Long> {

    List<LabMachine> findByOwnerIdAndIsDeletedFalseOrderByMachineNameAsc(Long ownerId);

    Optional<LabMachine> findByIdAndOwnerId(Long id, Long ownerId);

    long countByOwnerIdAndIsDeletedFalseAndStatus(Long ownerId, LabMachine.MachineStatus status);
}
