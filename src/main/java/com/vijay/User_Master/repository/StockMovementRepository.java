package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByInventoryItemIdAndOwnerIdOrderByCreatedOnDesc(Long inventoryItemId, Long ownerId);
}
