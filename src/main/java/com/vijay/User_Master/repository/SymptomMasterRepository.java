package com.vijay.User_Master.repository;

import com.vijay.User_Master.entity.SymptomMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for symptom master data
 */
@Repository
public interface SymptomMasterRepository extends JpaRepository<SymptomMaster, Long> {
    
    /**
     * Find symptoms by name (for autocomplete)
     */
    List<SymptomMaster> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find symptoms by category
     */
    List<SymptomMaster> findByCategory(String category);
}
