package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByOwnerId(Long ownerId);
    List<Bill> findByPatientId(Long patientId);
    Optional<Bill> findByBillNumber(String billNumber);
    long countByCreatedOnBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
}
