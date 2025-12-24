package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.FavouriteEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavouriteEntryRepo extends JpaRepository<FavouriteEntry, Long> {

    List<FavouriteEntry> findByUserId(Long userId);
}
