package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    
    Optional<Vendor> findByIdAndOwnerId(Long id, Long ownerId);

    Optional<Vendor> findByVendorCodeAndOwnerIdAndIsDeletedFalse(String vendorCode, Long ownerId);
    
    Optional<Vendor> findByEmailAndOwnerIdAndIsDeletedFalse(String email, Long ownerId);
    
    List<Vendor> findByOwnerIdAndIsDeletedFalse(Long ownerId);
    
    Page<Vendor> findByOwnerIdAndIsDeletedFalse(Long ownerId, Pageable pageable);
    
    List<Vendor> findByOwnerIdAndIsActiveTrueAndIsDeletedFalse(Long ownerId);
    
    @Query("SELECT v FROM Vendor v WHERE v.owner.id = :ownerId AND v.isDeleted = false AND (" +
           "LOWER(v.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.contactPerson) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.vendorCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Vendor> searchVendors(@Param("ownerId") Long ownerId, @Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT v FROM Vendor v WHERE v.owner.id = :ownerId AND v.isDeleted = false AND v.fulfillmentRate >= 95.0")
    List<Vendor> findReliableVendors(@Param("ownerId") Long ownerId);
    
    @Query("SELECT COUNT(v) FROM Vendor v WHERE v.owner.id = :ownerId AND v.isDeleted = false AND v.isActive = true")
    Long getActiveVendorCount(@Param("ownerId") Long ownerId);
}
