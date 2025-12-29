package com.vijay.User_Master.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prescription_dispensings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionDispensing extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispensed_by", nullable = false)
    private User dispensedBy; // Pharmacist who dispensed

    @Column(name = "dispensed_date")
    private LocalDateTime dispensedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispensingStatus status;

    @Column(length = 1000)
    private String notes;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "dispensing", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DispensedItem> dispensedItems = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public enum DispensingStatus {
        PENDING,      // Prescription received, not yet processed
        IN_PROGRESS,  // Pharmacist is processing
        PARTIAL,      // Some items dispensed, some unavailable
        COMPLETED,    // All items dispensed
        CANCELLED     // Dispensing cancelled
    }

    // Helper method to add dispensed item
    public void addDispensedItem(DispensedItem item) {
        dispensedItems.add(item);
        item.setDispensing(this);
    }

    // Helper method to calculate total
    public void calculateTotal() {
        this.totalAmount = dispensedItems.stream()
                .map(DispensedItem::getTotalPrice)
                .filter(price -> price != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
