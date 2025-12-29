package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.InventoryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    
    Optional<InventoryItem> findByIdAndOwnerId(Long id, Long ownerId);

    Optional<InventoryItem> findByItemCodeAndOwnerIdAndIsDeletedFalse(String itemCode, Long ownerId);
    
    List<InventoryItem> findByOwnerId(Long ownerId);
    
    Optional<InventoryItem> findByNameAndOwnerIdAndIsDeletedFalse(String name, Long ownerId);
    
    List<InventoryItem> findByOwnerIdAndIsDeletedFalse(Long ownerId);
    
    Page<InventoryItem> findByOwnerIdAndIsDeletedFalse(Long ownerId, Pageable pageable);
    
    List<InventoryItem> findByCategoryAndOwnerIdAndIsDeletedFalse(String category, Long ownerId);
    
    List<InventoryItem> findByOwnerIdAndIsActiveTrueAndIsDeletedFalse(Long ownerId);
    
    @Query("SELECT ii FROM InventoryItem ii WHERE ii.owner.id = :ownerId AND ii.isDeleted = false AND ii.currentStock <= ii.minStockLevel")
    List<InventoryItem> findLowStockItems(@Param("ownerId") Long ownerId);
    
    @Query("SELECT ii FROM InventoryItem ii WHERE ii.owner.id = :ownerId AND ii.isDeleted = false AND ii.currentStock <= ii.reorderLevel")
    List<InventoryItem> findItemsNeedingReorder(@Param("ownerId") Long ownerId);
    
    @Query("SELECT ii FROM InventoryItem ii WHERE ii.owner.id = :ownerId AND ii.isDeleted = false AND (" +
           "LOWER(ii.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(ii.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(ii.itemCode) LIKE LOWER(CONCAT('%', :keyword, '%')))" )
    Page<InventoryItem> searchItems(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT SUM(ii.currentStock * ii.unitPrice) FROM InventoryItem ii WHERE ii.owner.id = :ownerId AND ii.isDeleted = false")
    java.math.BigDecimal getTotalStockValue(@Param("ownerId") Long ownerId);
    
    @Query("SELECT COUNT(ii) FROM InventoryItem ii WHERE ii.owner.id = :ownerId AND ii.isDeleted = false AND ii.currentStock <= ii.minStockLevel")
    Long getLowStockCount(@Param("ownerId") Long ownerId);
    
    @Query("SELECT COUNT(ii) FROM InventoryItem ii WHERE ii.owner.id = :ownerId AND ii.isDeleted = false AND ii.currentStock <= ii.reorderLevel")
    Long getReorderCount(@Param("ownerId") Long ownerId);
}
