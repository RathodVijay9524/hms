package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long> {

    List<Ward> findByOwnerIdAndIsDeletedFalse(Long ownerId);

    Optional<Ward> findByIdAndOwnerId(Long id, Long ownerId);

    Optional<Ward> findByCodeAndOwnerIdAndIsDeletedFalse(String code, Long ownerId);
}
