package com.vijay.User_Master.service;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.inventory.InventoryDashboardStatsDTO;
import com.vijay.User_Master.entity.InventoryItem;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.InventoryItemRepository;
import com.vijay.User_Master.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    
    private final InventoryItemRepository inventoryItemRepository;
    private final UserRepository userRepository;
    
    // Dashboard Statistics
    @Transactional(readOnly = true)
    public InventoryDashboardStatsDTO getDashboardStats() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        BigDecimal totalStockValue = inventoryItemRepository.getTotalStockValue(ownerId);
        if (totalStockValue == null) {
            totalStockValue = BigDecimal.ZERO;
        }
        
        Long lowStockCount = inventoryItemRepository.getLowStockCount(ownerId);
        Long reorderCount = inventoryItemRepository.getReorderCount(ownerId);
        
        return InventoryDashboardStatsDTO.builder()
                .totalStockValue(totalStockValue)
                .lowStockCount(lowStockCount != null ? lowStockCount : 0L)
                .reorderCount(reorderCount != null ? reorderCount : 0L)
                .build();
    }
    
    // Inventory Item Management
    @Transactional
    public InventoryItem createInventoryItem(InventoryItem item) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        item.setOwner(owner);
        return inventoryItemRepository.save(item);
    }
    
    @Transactional(readOnly = true)
    public Page<InventoryItem> getAllInventoryItems(Pageable pageable) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return inventoryItemRepository.findByOwnerIdAndIsDeletedFalse(ownerId, pageable);
    }
    
    @Transactional(readOnly = true)
    public InventoryItem getInventoryItemById(Long id) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return inventoryItemRepository.findByIdAndOwnerId(id, ownerId)
                .filter(item -> !item.getIsDeleted())
                .orElseThrow(() -> new RuntimeException("Inventory item not found"));
    }
    
    @Transactional
    public InventoryItem updateInventoryItem(Long id, InventoryItem itemDetails) {
        InventoryItem item = getInventoryItemById(id);
        item.setName(itemDetails.getName());
        item.setDescription(itemDetails.getDescription());
        item.setCategory(itemDetails.getCategory());
        item.setUnitPrice(itemDetails.getUnitPrice());
        item.setMinStockLevel(itemDetails.getMinStockLevel());
        item.setMaxStockLevel(itemDetails.getMaxStockLevel());
        item.setReorderLevel(itemDetails.getReorderLevel());
        item.setUnitOfMeasure(itemDetails.getUnitOfMeasure());
        item.setBarcode(itemDetails.getBarcode());
        item.setIsActive(itemDetails.getIsActive());
        return inventoryItemRepository.save(item);
    }
    
    @Transactional
    public void deleteInventoryItem(Long id) {
        InventoryItem item = getInventoryItemById(id);
        item.setIsDeleted(true);
        inventoryItemRepository.save(item);
    }
    
    @Transactional(readOnly = true)
    public List<InventoryItem> getLowStockItems() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return inventoryItemRepository.findLowStockItems(ownerId);
    }
    
    @Transactional(readOnly = true)
    public List<InventoryItem> getItemsNeedingReorder() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return inventoryItemRepository.findItemsNeedingReorder(ownerId);
    }
    
    @Transactional(readOnly = true)
    public Page<InventoryItem> searchItems(String keyword, Pageable pageable) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return inventoryItemRepository.searchItems(ownerId, keyword, pageable);
    }
    
    @Transactional
    public void updateStockLevel(Long itemId, Integer newStock, Long performedBy) {
        InventoryItem item = getInventoryItemById(itemId);
        Integer oldStock = item.getCurrentStock();
        item.setCurrentStock(newStock);
        inventoryItemRepository.save(item);
        
        // TODO: Create stock movement record
    }
    
    @Transactional(readOnly = true)
    public List<InventoryItem> getItemsByCategory(String category) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return inventoryItemRepository.findByCategoryAndOwnerIdAndIsDeletedFalse(category, ownerId);
    }
}
