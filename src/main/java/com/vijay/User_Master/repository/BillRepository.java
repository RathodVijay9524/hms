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

    @org.springframework.data.jpa.repository.Query("SELECT SUM(b.netAmount) FROM Bill b WHERE b.owner.id = :ownerId AND b.status != 'CANCELLED'")
    Double sumTotalRevenue(Long ownerId);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(b.paidAmount) FROM Bill b WHERE b.owner.id = :ownerId AND b.status != 'CANCELLED'")
    Double sumTotalCollected(Long ownerId);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(b.balanceAmount) FROM Bill b WHERE b.owner.id = :ownerId AND b.status != 'CANCELLED'")
    Double sumPendingDues(Long ownerId);

    long countByOwnerIdAndCreatedOnBetween(Long ownerId, java.time.LocalDateTime start, java.time.LocalDateTime end);

    long countByOwnerIdAndStatus(Long ownerId, Bill.BillStatus status);
}
