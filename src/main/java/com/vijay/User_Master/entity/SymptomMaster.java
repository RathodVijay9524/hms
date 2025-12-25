package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Master data for common symptoms
 * Used for autocomplete suggestions
 */
@Entity
@Table(name = "symptom_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SymptomMaster extends BaseModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    
    @Column(length = 50)
    private String category; // e.g., "Respiratory", "Digestive", "Neurological"
}
