package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabResultRepository extends JpaRepository<LabResult, Long> {
    List<LabResult> findByOrder_IdAndOwner_Id(Long orderId, Long ownerId);
    Optional<LabResult> findByIdAndOwner_Id(Long id, Long ownerId);
}
