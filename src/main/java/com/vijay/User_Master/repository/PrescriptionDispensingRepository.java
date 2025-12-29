package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.PrescriptionDispensing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionDispensingRepository extends JpaRepository<PrescriptionDispensing, Long> {
    
    // Find all pending prescriptions for pharmacy queue
    List<PrescriptionDispensing> findByStatusAndOwnerIdOrderByCreatedOnAsc(
            PrescriptionDispensing.DispensingStatus status, Long ownerId);
    
    // Find by prescription
    Optional<PrescriptionDispensing> findByPrescriptionIdAndOwnerId(Long prescriptionId, Long ownerId);
    
    // Find dispensing history for a patient
    @Query("SELECT pd FROM PrescriptionDispensing pd " +
           "WHERE pd.prescription.visit.patient.id = :patientId " +
           "AND pd.owner.id = :ownerId " +
           "ORDER BY pd.dispensedDate DESC")
    List<PrescriptionDispensing> findByPatientId(@Param("patientId") Long patientId, 
                                                   @Param("ownerId") Long ownerId);
    
    // Count pending prescriptions
    long countByStatusAndOwnerId(PrescriptionDispensing.DispensingStatus status, Long ownerId);
    
    // Find dispensings within date range
    List<PrescriptionDispensing> findByDispensedDateBetweenAndOwnerId(
            LocalDateTime start, LocalDateTime end, Long ownerId);
    
    // Find all by owner
    List<PrescriptionDispensing> findByOwnerIdOrderByCreatedOnDesc(Long ownerId);

    Optional<PrescriptionDispensing> findByIdAndOwnerId(Long id, Long ownerId);
}
