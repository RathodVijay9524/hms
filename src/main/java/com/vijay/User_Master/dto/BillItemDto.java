package com.vijay.User_Master.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillItemDto {
    private Long id;
    private String itemName;
    private Integer quantity;
    private Double unitPrice;
    private Double taxPercent;
    private Double taxAmount;
    private Double totalAmount;
}
