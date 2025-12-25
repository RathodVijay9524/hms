package com.vijay.User_Master.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargeItemDto {
    private Long id;
    private String name;
    private String category;
    private Double baseAmount;
    private Double taxPercent;
    private Boolean active;
}
