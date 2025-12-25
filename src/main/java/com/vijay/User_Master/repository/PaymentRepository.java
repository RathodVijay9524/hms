package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBillId(Long billId);
    List<Payment> findByOwnerId(Long ownerId);
}
