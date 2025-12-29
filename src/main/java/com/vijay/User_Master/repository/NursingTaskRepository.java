package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.NursingTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NursingTaskRepository extends JpaRepository<NursingTask, Long> {

    Optional<NursingTask> findByIdAndOwnerId(Long id, Long ownerId);

    List<NursingTask> findByWardIdAndOwnerIdAndIsDeletedFalseAndShift(Long wardId, Long ownerId, String shift);

    Page<NursingTask> findByWardIdAndOwnerIdAndIsDeletedFalseAndShift(Long wardId, Long ownerId, String shift, Pageable pageable);

    long countByWardIdAndOwnerIdAndIsDeletedFalseAndShiftAndStatus(Long wardId, Long ownerId, String shift, NursingTask.TaskStatus status);
}
