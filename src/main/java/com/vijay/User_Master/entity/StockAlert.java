package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAlert extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType alertType; // LOW_STOCK, OUT_OF_STOCK

    @Column(nullable = false)
    private Integer currentStock;

    @Column(nullable = false)
    private Integer thresholdLevel;

    @Builder.Default
    private Boolean isResolved = false;
    private LocalDateTime resolvedDate;
    private Long resolvedBy;

    @Column(length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public enum AlertType {
        LOW_STOCK, OUT_OF_STOCK
    }
}
