package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "nursing_handovers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"owner_id", "ward_id", "handover_date", "from_shift", "to_shift"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class NursingHandover {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    private Ward ward;

    @Column(name = "handover_date", nullable = false)
    private LocalDate handoverDate;

    @Column(name = "from_shift", nullable = false)
    private String fromShift;

    @Column(name = "to_shift", nullable = false)
    private String toShift;

    @Column(name = "report_text", length = 8000)
    private String reportText;

    @Column(name = "checklist_json", length = 4000)
    private String checklistJson;

    @Column(name = "signed_off_by")
    private Long signedOffBy;

    @Column(name = "signed_off_at")
    private LocalDateTime signedOffAt;

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
        if (this.owner == null && this.ward != null) {
            this.owner = this.ward.getOwner();
        }
        if (this.handoverDate == null) {
            this.handoverDate = LocalDate.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Ward getWard() { return ward; }
    public void setWard(Ward ward) { this.ward = ward; }

    public LocalDate getHandoverDate() { return handoverDate; }
    public void setHandoverDate(LocalDate handoverDate) { this.handoverDate = handoverDate; }

    public String getFromShift() { return fromShift; }
    public void setFromShift(String fromShift) { this.fromShift = fromShift; }

    public String getToShift() { return toShift; }
    public void setToShift(String toShift) { this.toShift = toShift; }

    public String getReportText() { return reportText; }
    public void setReportText(String reportText) { this.reportText = reportText; }

    public String getChecklistJson() { return checklistJson; }
    public void setChecklistJson(String checklistJson) { this.checklistJson = checklistJson; }

    public Long getSignedOffBy() { return signedOffBy; }
    public void setSignedOffBy(Long signedOffBy) { this.signedOffBy = signedOffBy; }

    public LocalDateTime getSignedOffAt() { return signedOffAt; }
    public void setSignedOffAt(LocalDateTime signedOffAt) { this.signedOffAt = signedOffAt; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(LocalDateTime modifiedDate) { this.modifiedDate = modifiedDate; }
}
