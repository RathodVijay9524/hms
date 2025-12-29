package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "dispensed_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DispensedItem extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispensing_id", nullable = false)
    private PrescriptionDispensing dispensing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;

    @Column(name = "medicine_name", length = 200)
    private String medicineName; // Store medicine name for reference

    @Column(name = "quantity_dispensed", nullable = false)
    private Integer quantityDispensed;

    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "notes", length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @PrePersist
    @PreUpdate
    public void calculateTotal() {
        if (unitPrice != null && quantityDispensed != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantityDispensed));
        }
    }

    // Helper methods
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    public boolean isExpiringSoon(int daysThreshold) {
        if (expiryDate == null) return false;
        return expiryDate.isBefore(LocalDate.now().plusDays(daysThreshold));
    }
}
