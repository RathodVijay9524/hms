package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.LabOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabOrderRepository extends JpaRepository<LabOrder, Long> {
    Page<LabOrder> findByOwnerId(Long ownerId, Pageable pageable);
    Optional<LabOrder> findByIdAndOwnerId(Long id, Long ownerId);
    Optional<LabOrder> findByOrderNumberAndOwnerId(String orderNumber, Long ownerId);
    java.util.List<LabOrder> findByPatientIdAndOwnerIdOrderByCreatedOnDesc(Long patientId, Long ownerId);
    long countByOwnerIdAndStatusIn(Long ownerId, java.util.Collection<LabOrder.OrderStatus> statuses);
}
