package com.vijay.User_Master.service;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.inventory.InventoryDashboardStatsDTO;
import com.vijay.User_Master.dto.inventory.RequisitionKanbanDTO;
import com.vijay.User_Master.entity.InventoryItem;
import com.vijay.User_Master.entity.Requisition;
import com.vijay.User_Master.repository.InventoryItemRepository;
import com.vijay.User_Master.repository.PurchaseOrderRepository;
import com.vijay.User_Master.repository.RequisitionRepository;
import com.vijay.User_Master.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class InventoryDashboardService {
    
    private final InventoryItemRepository inventoryItemRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final RequisitionRepository requisitionRepository;
    private final VendorRepository vendorRepository;
    
    // Main Dashboard Statistics
    @Transactional(readOnly = true)
    public InventoryDashboardStatsDTO getCompleteDashboardStats() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        BigDecimal totalStockValue = inventoryItemRepository.getTotalStockValue(ownerId);
        if (totalStockValue == null) {
            totalStockValue = BigDecimal.ZERO;
        }
        
        Long lowStockCount = inventoryItemRepository.getLowStockCount(ownerId);
        Long reorderCount = inventoryItemRepository.getReorderCount(ownerId);
        Long pendingArrivals = purchaseOrderRepository.getPendingArrivalsCount(ownerId);
        BigDecimal pendingArrivalValue = purchaseOrderRepository.getPendingOrderValue(ownerId) != null ? 
                purchaseOrderRepository.getPendingOrderValue(ownerId) : BigDecimal.ZERO;
        Long activeVendors = vendorRepository.getActiveVendorCount(ownerId);
        
        return InventoryDashboardStatsDTO.builder()
                .totalStockValue(totalStockValue)
                .lowStockCount(lowStockCount != null ? lowStockCount : 0L)
                .reorderCount(reorderCount != null ? reorderCount : 0L)
                .pendingArrivals(pendingArrivals != null ? pendingArrivals : 0L)
                .pendingArrivalValue(pendingArrivalValue)
                .activeVendors(activeVendors != null ? activeVendors : 0L)
                .build();
    }
    
    // Stock Valuation Trends (for charts)
    @Transactional(readOnly = true)
    public Map<String, Object> getStockValuationTrends() {
        // Mock data for charts - in real implementation, this would query historical data
        List<String> labels = List.of("Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        List<BigDecimal> data = List.of(
                BigDecimal.valueOf(32), BigDecimal.valueOf(35), BigDecimal.valueOf(33),
                BigDecimal.valueOf(40), BigDecimal.valueOf(44), BigDecimal.valueOf(48.25)
        );
        
        Map<String, Object> trend = new HashMap<>();
        trend.put("labels", labels);
        trend.put("data", data);
        return trend;
    }
    
    // Departmental Requisitions Summary
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDepartmentalRequisitions() {
        List<Map<String, Object>> departments = new ArrayList<>();
        
        // Mock data based on frontend requirements
        Map<String, Object> pharmacy = new HashMap<>();
        pharmacy.put("name", "Pharmacy Hub");
        pharmacy.put("icon", "fas fa-pills");
        pharmacy.put("color", "blue");
        pharmacy.put("itemsRequested", 15);
        pharmacy.put("status", "Urgent");
        pharmacy.put("statusColor", "danger");
        departments.add(pharmacy);
        
        Map<String, Object> lab = new HashMap<>();
        lab.put("name", "Lab Diagnostics");
        lab.put("icon", "fas fa-microscope");
        lab.put("color", "amber");
        lab.put("itemsRequested", 8);
        lab.put("status", "Routine");
        lab.put("statusColor", "teal");
        departments.add(lab);
        
        Map<String, Object> nursing = new HashMap<>();
        nursing.put("name", "Nursing Wards");
        nursing.put("icon", "fas fa-user-nurse");
        nursing.put("color", "pink");
        nursing.put("itemsRequested", 22);
        nursing.put("status", "Pending");
        nursing.put("statusColor", "orange");
        departments.add(nursing);
        
        return departments;
    }
    
    // Critical Stock Alerts
    @Transactional(readOnly = true)
    public List<InventoryItem> getCriticalStockAlerts() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return inventoryItemRepository.findLowStockItems(ownerId);
    }
    
    // Recent Stock Movements
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRecentStockMovements() {
        // Mock data for recent activities
        List<Map<String, Object>> movements = new ArrayList<>();
        
        Map<String, Object> movement1 = new HashMap<>();
        movement1.put("time", "09:45 AM");
        movement1.put("type", "Stock In");
        movement1.put("item", "Syringes 5ml (BP)");
        movement1.put("quantity", 500);
        movement1.put("severity", "success");
        movements.add(movement1);
        
        Map<String, Object> movement2 = new HashMap<>();
        movement2.put("time", "10:12 AM");
        movement2.put("type", "Low Stock Alert");
        movement2.put("item", "Adrenaline Injections");
        movement2.put("quantity", 3);
        movement2.put("severity", "warning");
        movements.add(movement2);
        
        return movements;
    }
    
    // Purchase Order Status Summary
    @Transactional(readOnly = true)
    public Map<String, Long> getPurchaseOrderStatusSummary() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        Map<String, Long> summary = new HashMap<>();
        summary.put("draft", purchaseOrderRepository.countByStatus(ownerId, "DRAFT"));
        summary.put("approved", purchaseOrderRepository.countByStatus(ownerId, "APPROVED"));
        summary.put("shipped", purchaseOrderRepository.countByStatus(ownerId, "SHIPPED"));
        summary.put("received", purchaseOrderRepository.countByStatus(ownerId, "RECEIVED"));
        return summary;
    }
}
