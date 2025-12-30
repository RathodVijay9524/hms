package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_inventory_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabInventoryItem extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String itemName;

    private String manufacturer;

    @Column(nullable = false)
    private String category; // Biochemistry, Hematology, Microbiology, etc.

    private String batchNumber;

    @Column(nullable = false)
    private Integer currentStock;

    private Integer reorderLevel;

    private Integer maxStock;

    private String unit; // Units, mL, Kits, etc.

    private BigDecimal unitPrice;

    private LocalDate expiryDate;

    private LocalDate lastRestockedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StockStatus stockStatus = StockStatus.ADEQUATE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Builder.Default
    private Boolean isDeleted = false;

    public enum StockStatus {
        ADEQUATE, LOW, CRITICAL, OUT_OF_STOCK, EXPIRED
    }

    @PrePersist
    @PreUpdate
    public void updateStockStatus() {
        if (expiryDate != null && expiryDate.isBefore(LocalDate.now())) {
            this.stockStatus = StockStatus.EXPIRED;
        } else if (currentStock == 0) {
            this.stockStatus = StockStatus.OUT_OF_STOCK;
        } else if (reorderLevel != null && currentStock <= reorderLevel / 2) {
            this.stockStatus = StockStatus.CRITICAL;
        } else if (reorderLevel != null && currentStock <= reorderLevel) {
            this.stockStatus = StockStatus.LOW;
        } else {
            this.stockStatus = StockStatus.ADEQUATE;
        }
    }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
}
