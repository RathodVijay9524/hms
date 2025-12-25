package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByOwnerId(Long ownerId);
}
