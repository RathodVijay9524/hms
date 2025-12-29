package com.vijay.User_Master.service;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.config.security.CustomUserDetails;
import com.vijay.User_Master.dto.inventory.InventoryDashboardStatsDTO;
import com.vijay.User_Master.entity.InventoryItem;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.InventoryItemRepository;
import com.vijay.User_Master.repository.UserRepository;
import com.vijay.User_Master.repository.StockMovementRepository;
import com.vijay.User_Master.entity.StockMovement;
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
    private final StockMovementRepository stockMovementRepository;
    
    // Dashboard Statistics
    @Transactional(readOnly = true)
    public InventoryDashboardStatsDTO getDashboardStats() {
        CustomUserDetails user = CommonUtils.getLoggedInUser();
        if (user == null) {
            return InventoryDashboardStatsDTO.builder().build();
        }
        Long ownerId = user.getOwnerId();
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
        CustomUserDetails user = CommonUtils.getLoggedInUser();
        if (user == null) {
            throw new RuntimeException("User session not found. Please login again.");
        }
        Long ownerId = user.getOwnerId();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Ensure mandatory defaults
        if (item.getCurrentStock() == null) item.setCurrentStock(0);
        if (item.getMinStockLevel() == null) item.setMinStockLevel(0);
        if (item.getReorderLevel() == null) item.setReorderLevel(item.getMinStockLevel());
        if (item.getIsActive() == null) item.setIsActive(true);
        if (item.getIsDeleted() == null) item.setIsDeleted(false);
        if (item.getUnitPrice() == null) item.setUnitPrice(BigDecimal.ZERO);
        if (item.getUnitOfMeasure() == null) item.setUnitOfMeasure("unit");
        
        item.setOwner(owner);
        item.setCreatedBy(user.getId());
        return inventoryItemRepository.save(item);
    }
    
    @Transactional(readOnly = true)
    public Page<InventoryItem> getAllInventoryItems(Pageable pageable) {
        CustomUserDetails user = CommonUtils.getLoggedInUser();
        if (user == null) return Page.empty();
        Long ownerId = user.getOwnerId();
        return inventoryItemRepository.findByOwnerIdAndIsDeletedFalse(ownerId, pageable);
    }
    
    @Transactional(readOnly = true)
    public InventoryItem getInventoryItemById(Long id) {
        CustomUserDetails user = CommonUtils.getLoggedInUser();
        if (user == null) throw new RuntimeException("Session expired");
        Long ownerId = user.getOwnerId();
        return inventoryItemRepository.findByIdAndOwnerId(id, ownerId)
                .filter(item -> !item.getIsDeleted())
                .orElseThrow(() -> new RuntimeException("Inventory item not found"));
    }
    
    @Transactional
    public InventoryItem updateInventoryItem(Long id, InventoryItem itemDetails) {
        InventoryItem item = getInventoryItemById(id);
        if (itemDetails.getName() != null) item.setName(itemDetails.getName());
        if (itemDetails.getDescription() != null) item.setDescription(itemDetails.getDescription());
        if (itemDetails.getCategory() != null) item.setCategory(itemDetails.getCategory());
        if (itemDetails.getUnitPrice() != null) item.setUnitPrice(itemDetails.getUnitPrice());
        if (itemDetails.getMinStockLevel() != null) item.setMinStockLevel(itemDetails.getMinStockLevel());
        if (itemDetails.getMaxStockLevel() != null) item.setMaxStockLevel(itemDetails.getMaxStockLevel());
        if (itemDetails.getReorderLevel() != null) item.setReorderLevel(itemDetails.getReorderLevel());
        if (itemDetails.getUnitOfMeasure() != null) item.setUnitOfMeasure(itemDetails.getUnitOfMeasure());
        if (itemDetails.getBarcode() != null) item.setBarcode(itemDetails.getBarcode());
        if (itemDetails.getIsActive() != null) item.setIsActive(itemDetails.getIsActive());
        
        CustomUserDetails user = CommonUtils.getLoggedInUser();
        if (user != null) item.setModifiedBy(user.getId());
        
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
        Integer oldStock = item.getCurrentStock() != null ? item.getCurrentStock() : 0;
        item.setCurrentStock(newStock);
        inventoryItemRepository.save(item);
        
        StockMovement movement = new StockMovement();
        movement.setInventoryItem(item);
        movement.setMovementType(StockMovement.MovementType.ADJUSTMENT);
        movement.setQuantityChange(newStock - oldStock);
        movement.setQuantityBefore(oldStock);
        movement.setQuantityAfter(newStock);
        movement.setReferenceType("MANUAL_ADJUSTMENT");
        movement.setNotes("Manual stock adjustment");
        movement.setPerformedBy(performedBy);
        movement.setOwner(item.getOwner());
        stockMovementRepository.save(movement);
    }
    
    @Transactional(readOnly = true)
    public List<com.vijay.User_Master.dto.inventory.MedicineSuggestionDTO> getActiveMedicineSuggestions() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return inventoryItemRepository.findByOwnerIdAndIsActiveTrueAndIsDeletedFalse(ownerId).stream()
                .map(item -> com.vijay.User_Master.dto.inventory.MedicineSuggestionDTO.builder()
                        .name(item.getName())
                        .currentStock(item.getCurrentStock())
                        .unitOfMeasure(item.getUnitOfMeasure())
                        .build())
                .sorted(java.util.Comparator.comparing(com.vijay.User_Master.dto.inventory.MedicineSuggestionDTO::getName))
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getActiveMedicineNames() {
        return getActiveMedicineSuggestions().stream()
                .map(com.vijay.User_Master.dto.inventory.MedicineSuggestionDTO::getName)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InventoryItem> getItemsByCategory(String category) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return inventoryItemRepository.findByCategoryAndOwnerIdAndIsDeletedFalse(category, ownerId);
    }
}
