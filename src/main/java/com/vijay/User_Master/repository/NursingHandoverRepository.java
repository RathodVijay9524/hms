package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.NursingHandover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface NursingHandoverRepository extends JpaRepository<NursingHandover, Long> {

    Optional<NursingHandover> findByIdAndOwnerId(Long id, Long ownerId);

    Optional<NursingHandover> findByOwnerIdAndWardIdAndHandoverDateAndFromShiftAndToShift(
            Long ownerId,
            Long wardId,
            LocalDate handoverDate,
            String fromShift,
            String toShift
    );
}
