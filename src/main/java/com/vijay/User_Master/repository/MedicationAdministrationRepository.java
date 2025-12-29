package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.MedicationAdministration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicationAdministrationRepository extends JpaRepository<MedicationAdministration, Long> {

    Optional<MedicationAdministration> findByIdAndOwnerId(Long id, Long ownerId);

    @Query("SELECT ma FROM MedicationAdministration ma WHERE ma.owner.id = :ownerId AND ma.assignment.ward.id = :wardId AND ma.adminDate = :date")
    List<MedicationAdministration> findForWardDate(@Param("ownerId") Long ownerId, @Param("wardId") Long wardId, @Param("date") LocalDate date);

    @Query("SELECT ma FROM MedicationAdministration ma WHERE ma.owner.id = :ownerId AND ma.assignment.ward.id = :wardId AND ma.scheduledAt BETWEEN :from AND :to")
    List<MedicationAdministration> findForWardWindow(@Param("ownerId") Long ownerId, @Param("wardId") Long wardId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(ma) FROM MedicationAdministration ma WHERE ma.owner.id = :ownerId AND ma.assignment.ward.id = :wardId AND ma.adminDate = :date AND ma.status = :status")
    long countForWardDateAndStatus(@Param("ownerId") Long ownerId, @Param("wardId") Long wardId, @Param("date") LocalDate date, @Param("status") MedicationAdministration.AdminStatus status);

    boolean existsByAssignmentIdAndMedicineNameAndScheduledAtBetween(Long assignmentId, String medicineName, LocalDateTime start, LocalDateTime end);
}
