package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.inventory.InventoryDashboardStatsDTO;
import com.vijay.User_Master.entity.InventoryItem;
import com.vijay.User_Master.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InventoryController {
    
    private final InventoryService inventoryService;
    
    // Dashboard APIs
    @GetMapping("/dashboard/stats")
    public ResponseEntity<InventoryDashboardStatsDTO> getDashboardStats() {
        InventoryDashboardStatsDTO stats = inventoryService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/dashboard/alerts")
    public ResponseEntity<List<InventoryItem>> getLowStockAlerts() {
        List<InventoryItem> lowStockItems = inventoryService.getLowStockItems();
        return ResponseEntity.ok(lowStockItems);
    }
    
    @GetMapping("/dashboard/requisitions")
    public ResponseEntity<List<InventoryItem>> getItemsNeedingReorder() {
        List<InventoryItem> reorderItems = inventoryService.getItemsNeedingReorder();
        return ResponseEntity.ok(reorderItems);
    }
    
    // Inventory Item Management APIs
    @GetMapping("/items")
    public ResponseEntity<Page<InventoryItem>> getAllItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryItem> items = inventoryService.getAllInventoryItems(pageable);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/items/{id}")
    public ResponseEntity<InventoryItem> getItemById(@PathVariable Long id) {
        InventoryItem item = inventoryService.getInventoryItemById(id);
        return ResponseEntity.ok(item);
    }
    
    @PostMapping("/items")
    public ResponseEntity<InventoryItem> createItem(@RequestBody InventoryItem item) {
        InventoryItem created = inventoryService.createInventoryItem(item);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/items/{id}")
    public ResponseEntity<InventoryItem> updateItem(@PathVariable Long id, @RequestBody InventoryItem item) {
        InventoryItem updated = inventoryService.updateInventoryItem(id, item);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        inventoryService.deleteInventoryItem(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/items/search")
    public ResponseEntity<Page<InventoryItem>> searchItems(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryItem> items = inventoryService.searchItems(keyword, pageable);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/items/category/{category}")
    public ResponseEntity<List<InventoryItem>> getItemsByCategory(@PathVariable String category) {
        List<InventoryItem> items = inventoryService.getItemsByCategory(category);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/items/names")
    public ResponseEntity<List<com.vijay.User_Master.dto.inventory.MedicineSuggestionDTO>> getActiveMedicineSuggestions() {
        return ResponseEntity.ok(inventoryService.getActiveMedicineSuggestions());
    }

    @PatchMapping("/items/{id}/stock")
    public ResponseEntity<Void> updateStockLevel(
            @PathVariable Long id,
            @RequestParam Integer newStock,
            @RequestParam Long performedBy) {
        inventoryService.updateStockLevel(id, newStock, performedBy);
        return ResponseEntity.ok().build();
    }
}
