package com.vijay.User_Master.dto.pharmacy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyDashboardDTO {
    private long pendingPrescriptionsCount;
    private long lowStockCount;
    private long todayDispensedCount;
    private long expiringSoonCount;
    
    private List<DispensingDTO> recentPrescriptions; // Pending ones
    private List<DispensingDTO> recentDispensings;    // Completed ones
    private List<StockAlertDTO> stockAlerts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockAlertDTO {
        private String medicineName;
        private String sku;
        private int currentStock;
        private int minStock;
        private String type; // LOW_STOCK, OUT_OF_STOCK
    }
}
