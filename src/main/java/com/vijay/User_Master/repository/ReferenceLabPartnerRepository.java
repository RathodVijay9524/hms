package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.ReferenceLabPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReferenceLabPartnerRepository extends JpaRepository<ReferenceLabPartner, Long> {

    List<ReferenceLabPartner> findByOwnerIdAndIsDeletedFalseAndIsActiveTrueOrderByPartnerNameAsc(Long ownerId);

    List<ReferenceLabPartner> findByOwnerIdAndIsDeletedFalseOrderByPartnerNameAsc(Long ownerId);

    Optional<ReferenceLabPartner> findByIdAndOwnerId(Long id, Long ownerId);
}
