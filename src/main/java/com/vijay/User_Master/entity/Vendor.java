package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "vendors",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"owner_id", "vendor_code"}),
                @UniqueConstraint(columnNames = {"owner_id", "email"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Vendor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String vendorCode;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "contact_person")
    private String contactPerson;
    
    @Column
    private String email;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(length = 1000)
    private String address;
    
    @Column(name = "payment_terms")
    private String paymentTerms;
    
    @Column(name = "tax_number")
    private String taxNumber;
    
    @Column(name = "performance_rating", precision = 3, scale = 2)
    private BigDecimal performanceRating = BigDecimal.valueOf(5.0);
    
    @Column(name = "total_orders")
    private Integer totalOrders = 0;
    
    @Column(name = "fulfillment_rate", precision = 5, scale = 2)
    private BigDecimal fulfillmentRate = BigDecimal.valueOf(100.00);
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
    
    @LastModifiedDate
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "modified_by")
    private Long modifiedBy;
    
    // Helper methods
    public boolean isReliableVendor() {
        return fulfillmentRate.compareTo(BigDecimal.valueOf(95.0)) >= 0;
    }
    
    public void updatePerformance(int successfulOrders, int totalOrders) {
        this.totalOrders = totalOrders;
        if (totalOrders > 0) {
            this.fulfillmentRate = BigDecimal.valueOf(successfulOrders)
                    .divide(BigDecimal.valueOf(totalOrders), 2, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
    }
    
    // Manual getters and setters for compatibility
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getVendorCode() { return vendorCode; }
    public void setVendorCode(String vendorCode) { this.vendorCode = vendorCode; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }
    
    public String getTaxNumber() { return taxNumber; }
    public void setTaxNumber(String taxNumber) { this.taxNumber = taxNumber; }
    
    public BigDecimal getPerformanceRating() { return performanceRating; }
    public void setPerformanceRating(BigDecimal performanceRating) { this.performanceRating = performanceRating; }
    
    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
    
    public BigDecimal getFulfillmentRate() { return fulfillmentRate; }
    public void setFulfillmentRate(BigDecimal fulfillmentRate) { this.fulfillmentRate = fulfillmentRate; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(LocalDateTime modifiedDate) { this.modifiedDate = modifiedDate; }
    
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    
    public Long getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(Long modifiedBy) { this.modifiedBy = modifiedBy; }
}
