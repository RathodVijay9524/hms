package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class StockAlert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType alertType;
    
    @Column(name = "current_stock", nullable = false)
    private Integer currentStock;
    
    @Column(name = "threshold_level", nullable = false)
    private Integer thresholdLevel;
    
    @Column(name = "is_resolved", nullable = false)
    private Boolean isResolved = false;
    
    @Column(name = "resolved_date")
    private LocalDateTime resolvedDate;
    
    @Column(name = "resolved_by")
    private Long resolvedBy;
    
    @Column(length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
    
    @LastModifiedDate
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @PrePersist
    @PreUpdate
    private void syncOwner() {
        if (this.owner == null && this.inventoryItem != null) {
            this.owner = this.inventoryItem.getOwner();
        }
    }
    
    public enum AlertType {
        LOW_STOCK, OUT_OF_STOCK, EXPIRY_WARNING
    }
    
    // Manual getters and setters for compatibility
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public InventoryItem getInventoryItem() { return inventoryItem; }
    public void setInventoryItem(InventoryItem inventoryItem) { this.inventoryItem = inventoryItem; }
    
    public AlertType getAlertType() { return alertType; }
    public void setAlertType(AlertType alertType) { this.alertType = alertType; }
    
    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
    
    public Integer getThresholdLevel() { return thresholdLevel; }
    public void setThresholdLevel(Integer thresholdLevel) { this.thresholdLevel = thresholdLevel; }
    
    public Boolean getIsResolved() { return isResolved; }
    public void setIsResolved(Boolean isResolved) { this.isResolved = isResolved; }
    
    public LocalDateTime getResolvedDate() { return resolvedDate; }
    public void setResolvedDate(LocalDateTime resolvedDate) { this.resolvedDate = resolvedDate; }
    
    public Long getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(Long resolvedBy) { this.resolvedBy = resolvedBy; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(LocalDateTime modifiedDate) { this.modifiedDate = modifiedDate; }
}
