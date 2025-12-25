package com.vijay.User_Master.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingStatsDto {
    private Double totalRevenue;
    private Double totalCollected;
    private Double pendingDues;
    private Long billsToday;
    private Long partialPaymentsCount;
}
