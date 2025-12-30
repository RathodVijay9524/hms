package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.NursingAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NursingAlertRepository extends JpaRepository<NursingAlert, Long> {

    Optional<NursingAlert> findByIdAndOwnerId(Long id, Long ownerId);

    List<NursingAlert> findByOwnerIdAndWardIdAndIsAcknowledgedFalse(Long ownerId, Long wardId);

    @Query("SELECT COUNT(a) FROM NursingAlert a WHERE a.owner.id = :ownerId AND a.ward.id = :wardId AND a.isAcknowledged = false AND a.severity = 'CRITICAL'")
    long countCriticalOpen(@Param("ownerId") Long ownerId, @Param("wardId") Long wardId);

    // For doctor's critical alerts view - get all open alerts
    List<NursingAlert> findByOwnerIdAndIsAcknowledgedFalseOrderByCreatedDateDesc(Long ownerId);
}
