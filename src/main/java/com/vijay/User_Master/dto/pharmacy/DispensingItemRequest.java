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
public class DispensingItemRequest {
    private Long prescriptionItemId;
    private Long inventoryItemId;
    private Integer quantityDispensed;
    private BigDecimal unitPrice;
    private String batchNumber;
    private LocalDate expiryDate;
    private String notes;
}
