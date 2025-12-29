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
@Table(name = "medication_administrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MedicationAdministration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private WardPatientAssignment assignment;

    @Column(name = "prescription_id")
    private Long prescriptionId;

    @Column(name = "medicine_name", nullable = false)
    private String medicineName;

    @Column(name = "dosage")
    private String dosage;

    @Column(name = "route")
    private String route;

    @Column(name = "instructions")
    private String instructions;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminStatus status = AdminStatus.DUE;

    @Column(name = "administered_at")
    private LocalDateTime administeredAt;

    @Column(name = "administered_by")
    private Long administeredBy;

    @Column(length = 500)
    private String notes;

    @Column(name = "admin_date", nullable = false)
    private LocalDate adminDate;

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
        if (this.owner == null && this.assignment != null) {
            this.owner = this.assignment.getOwner();
        }
        if (this.adminDate == null && this.scheduledAt != null) {
            this.adminDate = this.scheduledAt.toLocalDate();
        }
    }

    public enum AdminStatus {
        DUE,
        DONE,
        SKIPPED
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public WardPatientAssignment getAssignment() { return assignment; }
    public void setAssignment(WardPatientAssignment assignment) { this.assignment = assignment; }

    public Long getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(Long prescriptionId) { this.prescriptionId = prescriptionId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public AdminStatus getStatus() { return status; }
    public void setStatus(AdminStatus status) { this.status = status; }

    public LocalDateTime getAdministeredAt() { return administeredAt; }
    public void setAdministeredAt(LocalDateTime administeredAt) { this.administeredAt = administeredAt; }

    public Long getAdministeredBy() { return administeredBy; }
    public void setAdministeredBy(Long administeredBy) { this.administeredBy = administeredBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDate getAdminDate() { return adminDate; }
    public void setAdminDate(LocalDate adminDate) { this.adminDate = adminDate; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(LocalDateTime modifiedDate) { this.modifiedDate = modifiedDate; }
}
