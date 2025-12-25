package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.ChargeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargeItemRepository extends JpaRepository<ChargeItem, Long> {
    List<ChargeItem> findByOwnerId(Long ownerId);
    List<ChargeItem> findByOwnerIdAndActiveTrue(Long ownerId);
    List<ChargeItem> findByCategoryAndOwnerId(String category, Long ownerId);
}
