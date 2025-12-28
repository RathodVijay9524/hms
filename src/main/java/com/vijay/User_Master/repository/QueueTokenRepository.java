package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.QueueToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface QueueTokenRepository extends JpaRepository<QueueToken, Long> {
    List<QueueToken> findByOwnerId(Long ownerId);
    List<QueueToken> findByOwnerIdAndTokenDate(Long ownerId, LocalDate date);
    long countByOwnerIdAndTokenDate(Long ownerId, LocalDate date);
}
