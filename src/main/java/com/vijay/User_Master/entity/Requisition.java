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
@Table(
        name = "requisitions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"owner_id", "req_number"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Requisition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "req_number", nullable = false)
    private String reqNumber;
    
    @Column(name = "requesting_department", nullable = false)
    private String requestingDepartment;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequisitionStatus status = RequisitionStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequisitionPriority priority = RequisitionPriority.ROUTINE;
    
    @Column(name = "requested_by", nullable = false)
    private Long requestedBy;
    
    @Column(name = "approved_by")
    private Long approvedBy;
    
    @Column(name = "approved_date")
    private LocalDateTime approvedDate;
    
    @Column(name = "fulfilled_date")
    private LocalDateTime fulfilledDate;
    
    @Column(name = "expected_date")
    private LocalDateTime expectedDate;
    
    @Column(length = 1000)
    private String notes;
    
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
    public boolean isPending() {
        return status == RequisitionStatus.PENDING;
    }
    
    public boolean isCritical() {
        return priority == RequisitionPriority.CRITICAL;
    }
    
    public boolean isFulfilled() {
        return status == RequisitionStatus.FULFILLED;
    }
    
    // Manual getters and setters for compatibility
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getReqNumber() { return reqNumber; }
    public void setReqNumber(String reqNumber) { this.reqNumber = reqNumber; }
    
    public String getRequestingDepartment() { return requestingDepartment; }
    public void setRequestingDepartment(String requestingDepartment) { this.requestingDepartment = requestingDepartment; }
    
    public RequisitionStatus getStatus() { return status; }
    public void setStatus(RequisitionStatus status) { this.status = status; }
    
    public RequisitionPriority getPriority() { return priority; }
    public void setPriority(RequisitionPriority priority) { this.priority = priority; }
    
    public Long getRequestedBy() { return requestedBy; }
    public void setRequestedBy(Long requestedBy) { this.requestedBy = requestedBy; }
    
    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }
    
    public LocalDateTime getApprovedDate() { return approvedDate; }
    public void setApprovedDate(LocalDateTime approvedDate) { this.approvedDate = approvedDate; }
    
    public LocalDateTime getFulfilledDate() { return fulfilledDate; }
    public void setFulfilledDate(LocalDateTime fulfilledDate) { this.fulfilledDate = fulfilledDate; }
    
    public LocalDateTime getExpectedDate() { return expectedDate; }
    public void setExpectedDate(LocalDateTime expectedDate) { this.expectedDate = expectedDate; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
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
    
    public enum RequisitionStatus {
        PENDING, APPROVED, FULFILLED, CANCELLED
    }
    
    public enum RequisitionPriority {
        CRITICAL, ROUTINE, LOW
    }
}
