package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.StockAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockAlertRepository extends JpaRepository<StockAlert, Long> {
    List<StockAlert> findByInventoryItemIdAndOwnerIdAndIsResolvedFalse(Long inventoryItemId, Long ownerId);
    Optional<StockAlert> findFirstByInventoryItemIdAndOwnerIdAndIsResolvedFalseAndAlertType(Long inventoryItemId, Long ownerId, StockAlert.AlertType alertType);
    List<StockAlert> findByOwnerIdAndIsResolvedFalse(Long ownerId);
}
