package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "charge_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargeItem extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category; // e.g., CONSULTATION, LAB_TEST, PROCEDURE

    @Column(nullable = false)
    private Double baseAmount;

    @Column(nullable = false)
    private Double taxPercent; // e.g., 5.0, 12.0, 18.0

    @Builder.Default
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner; // Tenant segregation
}
