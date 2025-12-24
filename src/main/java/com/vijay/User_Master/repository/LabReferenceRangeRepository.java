package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.LabReferenceRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabReferenceRangeRepository extends JpaRepository<LabReferenceRange, Long> {
}
