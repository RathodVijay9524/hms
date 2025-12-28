package com.vijay.User_Master.controller;

import com.vijay.User_Master.dto.inventory.RequisitionKanbanDTO;
import com.vijay.User_Master.entity.Requisition;
import com.vijay.User_Master.service.RequisitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/requisitions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RequisitionController {
    
    private final RequisitionService requisitionService;
    
    // Dashboard APIs
    @GetMapping("/dashboard/pending-count")
    public ResponseEntity<Long> getPendingRequisitionsCount() {
        Long count = requisitionService.getPendingRequisitionsCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/dashboard/critical-count")
    public ResponseEntity<Long> getCriticalRequisitionsCount() {
        Long count = requisitionService.getCriticalRequisitionsCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/dashboard/kanban")
    public ResponseEntity<RequisitionKanbanDTO> getKanbanView() {
        RequisitionKanbanDTO kanban = requisitionService.getKanbanView();
        return ResponseEntity.ok(kanban);
    }
    
    // Requisition Management APIs
    @GetMapping
    public ResponseEntity<Page<Requisition>> getAllRequisitions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Requisition> requisitions = requisitionService.getAllRequisitions(pageable);
        return ResponseEntity.ok(requisitions);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Requisition> getRequisitionById(@PathVariable Long id) {
        Requisition requisition = requisitionService.getRequisitionById(id);
        return ResponseEntity.ok(requisition);
    }
    
    @PostMapping
    public ResponseEntity<Requisition> createRequisition(@RequestBody Requisition requisition) {
        Requisition created = requisitionService.createRequisition(requisition);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Requisition> updateRequisition(@PathVariable Long id, @RequestBody Requisition requisition) {
        Requisition updated = requisitionService.updateRequisition(id, requisition);
        return ResponseEntity.ok(updated);
    }
    
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approveRequisition(@PathVariable Long id, @RequestParam Long approvedBy) {
        requisitionService.approveRequisition(id, approvedBy);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}/fulfill")
    public ResponseEntity<Void> fulfillRequisition(@PathVariable Long id) {
        requisitionService.fulfillRequisition(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/department/{department}")
    public ResponseEntity<List<Requisition>> getRequisitionsByDepartment(@PathVariable String department) {
        List<Requisition> requisitions = requisitionService.getRequisitionsByDepartment(department);
        return ResponseEntity.ok(requisitions);
    }
}
