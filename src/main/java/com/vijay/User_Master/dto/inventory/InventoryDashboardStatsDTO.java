package com.vijay.User_Master.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDashboardStatsDTO {
    
    private BigDecimal totalStockValue;
    private Long lowStockCount;
    private Long reorderCount;
    private Long pendingArrivals;
    private BigDecimal pendingArrivalValue;
    private Long activeVendors;
    
    // Manual getters and setters for compatibility
    public BigDecimal getTotalStockValue() { return totalStockValue; }
    public void setTotalStockValue(BigDecimal totalStockValue) { this.totalStockValue = totalStockValue; }
    
    public Long getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(Long lowStockCount) { this.lowStockCount = lowStockCount; }
    
    public Long getReorderCount() { return reorderCount; }
    public void setReorderCount(Long reorderCount) { this.reorderCount = reorderCount; }
    
    public Long getPendingArrivals() { return pendingArrivals; }
    public void setPendingArrivals(Long pendingArrivals) { this.pendingArrivals = pendingArrivals; }
    
    public BigDecimal getPendingArrivalValue() { return pendingArrivalValue; }
    public void setPendingArrivalValue(BigDecimal pendingArrivalValue) { this.pendingArrivalValue = pendingArrivalValue; }
    
    public Long getActiveVendors() { return activeVendors; }
    public void setActiveVendors(Long activeVendors) { this.activeVendors = activeVendors; }
}
