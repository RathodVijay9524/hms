package com.vijay.User_Master.dto.pharmacy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DispensedItemDTO {
    private Long id;
    private Long prescriptionItemId;
    private Long inventoryItemId;
    private String medicineName;
    private Integer quantityPrescribed;
    private Integer quantityDispensed;
    private String dosage;
    private String duration;
    private String instructions;
    private Integer quantityAvailable; // From inventory
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String batchNumber;
    private LocalDate expiryDate;
    private String notes;
    private boolean inStock;
}
