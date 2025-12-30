package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Surgery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SurgeryRepository extends JpaRepository<Surgery, Long> {

    List<Surgery> findByLeadDoctorIdAndOwnerIdAndScheduledStartTimeBetween(
            Long doctorId,
            Long ownerId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Surgery> findByOwnerIdAndScheduledStartTimeBetween(
            Long ownerId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Surgery> findByOtCodeAndOwnerIdAndScheduledStartTimeBetween(
            String otCode,
            Long ownerId,
            LocalDateTime start,
            LocalDateTime end
    );
    
    long countByOwnerIdAndStatus(Long ownerId, Surgery.SurgeryStatus status);
}
