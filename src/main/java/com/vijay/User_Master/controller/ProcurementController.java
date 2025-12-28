package com.vijay.User_Master.controller;

import com.vijay.User_Master.entity.PurchaseOrder;
import com.vijay.User_Master.service.ProcurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/procurement")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProcurementController {
    
    private final ProcurementService procurementService;
    
    // Dashboard APIs
    @GetMapping("/dashboard/pending-arrivals")
    public ResponseEntity<Long> getPendingArrivalsCount() {
        Long count = procurementService.getPendingArrivalsCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/dashboard/pending-value")
    public ResponseEntity<java.math.BigDecimal> getPendingOrderValue() {
        java.math.BigDecimal value = procurementService.getPendingOrderValue();
        return ResponseEntity.ok(value);
    }
    
    // Purchase Order Management APIs
    @GetMapping("/purchase-orders")
    public ResponseEntity<Page<PurchaseOrder>> getAllPurchaseOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PurchaseOrder> orders = procurementService.getAllPurchaseOrders(pageable);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/purchase-orders/{id}")
    public ResponseEntity<PurchaseOrder> getPurchaseOrderById(@PathVariable Long id) {
        PurchaseOrder order = procurementService.getPurchaseOrderById(id);
        return ResponseEntity.ok(order);
    }
    
    @PostMapping("/purchase-orders")
    public ResponseEntity<PurchaseOrder> createPurchaseOrder(@RequestBody PurchaseOrder order) {
        PurchaseOrder created = procurementService.createPurchaseOrder(order);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/purchase-orders/{id}")
    public ResponseEntity<PurchaseOrder> updatePurchaseOrder(@PathVariable Long id, @RequestBody PurchaseOrder order) {
        PurchaseOrder updated = procurementService.updatePurchaseOrder(id, order);
        return ResponseEntity.ok(updated);
    }
    
    @PatchMapping("/purchase-orders/{id}/status")
    public ResponseEntity<Void> updatePurchaseOrderStatus(
            @PathVariable Long id,
            @RequestParam PurchaseOrder.POStatus status) {
        procurementService.updatePurchaseOrderStatus(id, status);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/purchase-orders/search")
    public ResponseEntity<Page<PurchaseOrder>> searchPurchaseOrders(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PurchaseOrder> orders = procurementService.searchPurchaseOrders(keyword, pageable);
        return ResponseEntity.ok(orders);
    }
}
