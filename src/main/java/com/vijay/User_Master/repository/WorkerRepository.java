package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Worker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long>, JpaSpecificationExecutor<Worker> {


   // Worker findByEmail(String email);
    Optional<Worker> findByEmail(String email);

    Optional<Worker> findByUsernameOrEmail(String username, String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);


    Page<Worker> findByCreatedByAndIsDeletedFalse(Long createdBy, Pageable pageable);

    // Find all workers created by a specific user and marked as deleted
    Page<Worker> findByCreatedByAndIsDeletedTrue(Long createdBy, Pageable pageable);

    // Find all workers by a specific status
    List<Worker> findByAccountStatus_IsActive(Boolean isActive);

    // Find all workers by name containing a specific keyword (case insensitive)
    List<Worker> findByNameContainingIgnoreCase(String name);

    // Find all workers by username
    List<Worker> findByUsername(String username);

    Page<Worker> findByUser_Id(Long loggedInUserId, Pageable pageable);
    // All workers created by a specific user
    // Active workers
    Page<Worker> findByUser_IdAndIsDeletedFalseAndAccountStatus_IsActiveTrue(Long userId, Pageable pageable);

    // Deleted workers
    Page<Worker> findByUser_IdAndIsDeletedTrue(Long userId, Pageable pageable);

    // Expired workers (not active but not deleted)
    Page<Worker> findByUser_IdAndIsDeletedFalseAndAccountStatus_IsActiveFalse(Long userId, Pageable pageable);

    @Query("SELECT w FROM Worker w WHERE " +
            "(:keyword IS NULL OR w.name LIKE %:keyword% OR w.username LIKE %:keyword% OR w.email LIKE %:keyword%) AND " +
            "(:isDeleted IS NULL OR w.isDeleted = :isDeleted) AND " +
            "(:isActive IS NULL OR w.accountStatus.isActive = :isActive) AND " +
            "(:userId IS NULL OR w.user.id = :userId)")
    Page<Worker> searchWorkersBySuperUserWithKeyword(@Param("keyword") String keyword,
                                                     @Param("isDeleted") Boolean isDeleted,
                                                     @Param("isActive") Boolean isActive,
                                                     @Param("userId") Long superUserId,
                                                     Pageable pageable);

    Page<Worker> findByUser_IdAndIsDeletedAndAccountStatus_IsActive(Long userId, boolean isDeleted, boolean isActive, Pageable pageable);

    Page<Worker> findByUser_IdAndIsDeleted(Long userId, boolean isDeleted, Pageable pageable);

    Page<Worker> findByUser_IdAndAccountStatus_IsActive(Long userId, boolean isActive, Pageable pageable);



}


