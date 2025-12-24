package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.LabTest;
import com.vijay.User_Master.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabTestRepository extends JpaRepository<LabTest, Long> {
    List<LabTest> findByOwnerIdAndActiveTrue(Long ownerId);
    Optional<LabTest> findByIdAndOwnerId(Long id, Long ownerId);
    boolean existsByNameAndOwnerId(String name, Long ownerId);
    long countByOwnerIdAndActiveTrue(Long ownerId);
}
