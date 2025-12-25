package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {
    List<DoctorProfile> findByOwnerId(Long ownerId);
    Optional<DoctorProfile> findByUserIdAndOwnerId(Long userId, Long ownerId);
}
