package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.LabParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabParameterRepository extends JpaRepository<LabParameter, Long> {
}
