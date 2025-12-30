package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.LabInventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LabInventoryRepository extends JpaRepository<LabInventoryItem, Long> {

    List<LabInventoryItem> findByOwnerIdAndIsDeletedFalseOrderByItemNameAsc(Long ownerId);

    List<LabInventoryItem> findByOwnerIdAndCategoryAndIsDeletedFalse(Long ownerId, String category);

    @Query("SELECT i FROM LabInventoryItem i WHERE i.owner.id = :ownerId AND i.isDeleted = false AND i.stockStatus IN ('LOW', 'CRITICAL', 'OUT_OF_STOCK')")
    List<LabInventoryItem> findLowStockItems(@Param("ownerId") Long ownerId);

    @Query("SELECT i FROM LabInventoryItem i WHERE i.owner.id = :ownerId AND i.isDeleted = false AND i.expiryDate <= :expiryThreshold")
    List<LabInventoryItem> findExpiringSoon(@Param("ownerId") Long ownerId, @Param("expiryThreshold") LocalDate expiryThreshold);

    @Query("SELECT COUNT(i) FROM LabInventoryItem i WHERE i.owner.id = :ownerId AND i.isDeleted = false AND i.stockStatus IN ('LOW', 'CRITICAL')")
    long countLowStock(@Param("ownerId") Long ownerId);

    @Query("SELECT COUNT(i) FROM LabInventoryItem i WHERE i.owner.id = :ownerId AND i.isDeleted = false AND i.expiryDate <= :expiryThreshold AND i.expiryDate > :today")
    long countExpiringSoon(@Param("ownerId") Long ownerId, @Param("today") LocalDate today, @Param("expiryThreshold") LocalDate expiryThreshold);

    @Query("SELECT COALESCE(SUM(i.currentStock * i.unitPrice), 0) FROM LabInventoryItem i WHERE i.owner.id = :ownerId AND i.isDeleted = false")
    java.math.BigDecimal getTotalValuation(@Param("ownerId") Long ownerId);

    long countByOwnerIdAndIsDeletedFalse(Long ownerId);
}
