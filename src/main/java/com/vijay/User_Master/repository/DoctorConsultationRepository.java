package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.DoctorConsultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorConsultationRepository extends JpaRepository<DoctorConsultation, Long> {

    List<DoctorConsultation> findByRecipientIdAndOwnerIdOrderByRequestedAtDesc(Long recipientId, Long ownerId);

    List<DoctorConsultation> findByRequesterIdAndOwnerIdOrderByRequestedAtDesc(Long requesterId, Long ownerId);

    long countByRecipientIdAndOwnerIdAndStatus(Long recipientId, Long ownerId, DoctorConsultation.ConsultationStatus status);
    
    long countByRecipientIdAndOwnerIdAndStatusIn(Long recipientId, Long ownerId, List<DoctorConsultation.ConsultationStatus> statuses);
}
