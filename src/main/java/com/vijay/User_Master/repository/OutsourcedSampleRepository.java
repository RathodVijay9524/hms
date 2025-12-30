package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.OutsourcedSample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OutsourcedSampleRepository extends JpaRepository<OutsourcedSample, Long> {

    List<OutsourcedSample> findByOwnerIdAndIsDeletedFalseOrderBySentDateTimeDesc(Long ownerId);

    List<OutsourcedSample> findByOwnerIdAndStatusAndIsDeletedFalse(Long ownerId, OutsourcedSample.SampleStatus status);

    Optional<OutsourcedSample> findByIdAndOwnerId(Long id, Long ownerId);

    Optional<OutsourcedSample> findBySampleIdAndOwnerId(String sampleId, Long ownerId);

    @Query("SELECT COUNT(s) FROM OutsourcedSample s WHERE s.owner.id = :ownerId AND s.isDeleted = false AND s.status NOT IN ('COMPLETED', 'REJECTED')")
    long countActive(@Param("ownerId") Long ownerId);

    @Query("SELECT COUNT(s) FROM OutsourcedSample s WHERE s.owner.id = :ownerId AND s.partner.id = :partnerId AND s.isDeleted = false AND s.status NOT IN ('COMPLETED', 'REJECTED')")
    long countActiveByPartner(@Param("ownerId") Long ownerId, @Param("partnerId") Long partnerId);
}
