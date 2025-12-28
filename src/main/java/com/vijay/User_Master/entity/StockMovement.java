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
@Table(name = "stock_movements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class StockMovement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType movementType;
    
    @Column(name = "quantity_change", nullable = false)
    private Integer quantityChange;
    
    @Column(name = "quantity_before", nullable = false)
    private Integer quantityBefore;
    
    @Column(name = "quantity_after", nullable = false)
    private Integer quantityAfter;
    
    @Column(name = "reference_type")
    private String referenceType; // PO, REQUISITION, ADJUSTMENT, etc.
    
    @Column(name = "reference_id")
    private Long referenceId;
    
    @Column(length = 500)
    private String notes;
    
    @Column(name = "performed_by", nullable = false)
    private Long performedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @CreatedDate
    @Column(name = "movement_date", updatable = false)
    private LocalDateTime movementDate;
    
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
    
    public enum MovementType {
        IN, OUT, ADJUSTMENT, TRANSFER
    }
    
    // Manual getters and setters for compatibility
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public InventoryItem getInventoryItem() { return inventoryItem; }
    public void setInventoryItem(InventoryItem inventoryItem) { this.inventoryItem = inventoryItem; }
    
    public MovementType getMovementType() { return movementType; }
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }
    
    public Integer getQuantityChange() { return quantityChange; }
    public void setQuantityChange(Integer quantityChange) { this.quantityChange = quantityChange; }
    
    public Integer getQuantityBefore() { return quantityBefore; }
    public void setQuantityBefore(Integer quantityBefore) { this.quantityBefore = quantityBefore; }
    
    public Integer getQuantityAfter() { return quantityAfter; }
    public void setQuantityAfter(Integer quantityAfter) { this.quantityAfter = quantityAfter; }
    
    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }
    
    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Long getPerformedBy() { return performedBy; }
    public void setPerformedBy(Long performedBy) { this.performedBy = performedBy; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    
    public LocalDateTime getMovementDate() { return movementDate; }
    public void setMovementDate(LocalDateTime movementDate) { this.movementDate = movementDate; }
    
    public LocalDateTime getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(LocalDateTime modifiedDate) { this.modifiedDate = modifiedDate; }
}
