package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    
    Optional<PurchaseOrder> findByIdAndOwnerId(Long id, Long ownerId);

    Optional<PurchaseOrder> findByPoNumberAndOwnerIdAndIsDeletedFalse(String poNumber, Long ownerId);
    
    List<PurchaseOrder> findByOwnerIdAndIsDeletedFalse(Long ownerId);
    
    Page<PurchaseOrder> findByOwnerIdAndIsDeletedFalse(Long ownerId, Pageable pageable);
    
    List<PurchaseOrder> findByOwnerIdAndStatusAndIsDeletedFalse(Long ownerId, PurchaseOrder.POStatus status);
    
    List<PurchaseOrder> findByOwnerIdAndVendorIdAndIsDeletedFalse(Long ownerId, Long vendorId);

    long countByOwnerId(Long ownerId);
    
    @Query("SELECT po FROM PurchaseOrder po WHERE po.owner.id = :ownerId AND po.isDeleted = false AND " +
           "LOWER(po.poNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<PurchaseOrder> searchPurchaseOrders(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT po FROM PurchaseOrder po WHERE po.owner.id = :ownerId AND po.isDeleted = false AND po.status = :status")
    Page<PurchaseOrder> findByStatus(@Param("ownerId") Long ownerId, @Param("status") PurchaseOrder.POStatus status, Pageable pageable);
    
    @Query("SELECT COUNT(po) FROM PurchaseOrder po WHERE po.owner.id = :ownerId AND po.isDeleted = false AND po.status = 'SHIPPED'")
    Long getPendingArrivalsCount(@Param("ownerId") Long ownerId);
    
    @Query("SELECT SUM(po.totalAmount) FROM PurchaseOrder po WHERE po.owner.id = :ownerId AND po.isDeleted = false AND po.status = 'APPROVED'")
    java.math.BigDecimal getPendingOrderValue(@Param("ownerId") Long ownerId);
    
    @Query("SELECT po FROM PurchaseOrder po WHERE po.owner.id = :ownerId AND po.isDeleted = false AND po.expectedDeliveryDate <= :date")
    List<PurchaseOrder> findOverdueOrders(@Param("ownerId") Long ownerId, @Param("date") LocalDateTime date);
    
    // Additional methods for dashboard
    @Query("SELECT COUNT(po) FROM PurchaseOrder po WHERE po.owner.id = :ownerId AND po.isDeleted = false AND po.status = :status")
    Long countByStatus(@Param("ownerId") Long ownerId, @Param("status") String status);
}
