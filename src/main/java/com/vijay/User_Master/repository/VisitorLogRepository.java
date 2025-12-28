package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.VisitorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VisitorLogRepository extends JpaRepository<VisitorLog, Long> {
    List<VisitorLog> findByOwnerId(Long ownerId);
}
