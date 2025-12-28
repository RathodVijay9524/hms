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
@Table(name = "requisition_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class RequisitionItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requisition_id", nullable = false)
    private Requisition requisition;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;
    
    @Column(name = "quantity_requested", nullable = false)
    private Integer quantityRequested;
    
    @Column(name = "quantity_fulfilled")
    private Integer quantityFulfilled = 0;
    
    @Column(name = "unit_of_measure", nullable = false)
    private String unitOfMeasure;
    
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
        if (this.owner == null && this.requisition != null) {
            this.owner = this.requisition.getOwner();
        }
    }
    
    public boolean isFullyFulfilled() {
        return quantityFulfilled != null && quantityFulfilled >= quantityRequested;
    }
    
    public boolean isPartiallyFulfilled() {
        return quantityFulfilled != null && quantityFulfilled > 0 && quantityFulfilled < quantityRequested;
    }
    
    // Manual getters and setters for compatibility
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Requisition getRequisition() { return requisition; }
    public void setRequisition(Requisition requisition) { this.requisition = requisition; }
    
    public InventoryItem getInventoryItem() { return inventoryItem; }
    public void setInventoryItem(InventoryItem inventoryItem) { this.inventoryItem = inventoryItem; }
    
    public Integer getQuantityRequested() { return quantityRequested; }
    public void setQuantityRequested(Integer quantityRequested) { this.quantityRequested = quantityRequested; }
    
    public Integer getQuantityFulfilled() { return quantityFulfilled; }
    public void setQuantityFulfilled(Integer quantityFulfilled) { this.quantityFulfilled = quantityFulfilled; }
    
    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(LocalDateTime modifiedDate) { this.modifiedDate = modifiedDate; }
}
