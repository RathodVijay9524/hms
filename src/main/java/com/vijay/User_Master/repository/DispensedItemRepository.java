package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.DispensedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DispensedItemRepository extends JpaRepository<DispensedItem, Long> {
    
    // Find all items for a dispensing
    List<DispensedItem> findByDispensingId(Long dispensingId);
    
    // Find items expiring soon
    @Query("SELECT di FROM DispensedItem di " +
           "WHERE di.expiryDate BETWEEN :startDate AND :endDate " +
           "AND di.owner.id = :ownerId")
    List<DispensedItem> findExpiringSoon(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate,
                                         @Param("ownerId") Long ownerId);
    
    // Find all items by inventory item (for tracking)
    List<DispensedItem> findByInventoryItemIdAndOwnerId(Long inventoryItemId, Long ownerId);
}
