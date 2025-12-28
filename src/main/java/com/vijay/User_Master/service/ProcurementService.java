package com.vijay.User_Master.service;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.entity.PurchaseOrder;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.PurchaseOrderRepository;
import com.vijay.User_Master.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcurementService {
    
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final UserRepository userRepository;
    
    // Dashboard Statistics
    @Transactional(readOnly = true)
    public Long getPendingArrivalsCount() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return purchaseOrderRepository.getPendingArrivalsCount(ownerId);
    }
    
    @Transactional(readOnly = true)
    public java.math.BigDecimal getPendingOrderValue() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        java.math.BigDecimal value = purchaseOrderRepository.getPendingOrderValue(ownerId);
        return value != null ? value : java.math.BigDecimal.ZERO;
    }
    
    // Purchase Order Management
    @Transactional
    public PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate PO number
        String poNumber = generatePONumber(ownerId);
        purchaseOrder.setPoNumber(poNumber);
        purchaseOrder.setOrderDate(java.time.LocalDateTime.now());
        purchaseOrder.setOwner(owner);
        return purchaseOrderRepository.save(purchaseOrder);
    }
    
    @Transactional(readOnly = true)
    public Page<PurchaseOrder> getAllPurchaseOrders(Pageable pageable) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return purchaseOrderRepository.findByOwnerIdAndIsDeletedFalse(ownerId, pageable);
    }
    
    @Transactional(readOnly = true)
    public PurchaseOrder getPurchaseOrderById(Long id) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return purchaseOrderRepository.findByIdAndOwnerId(id, ownerId)
                .filter(po -> !po.getIsDeleted())
                .orElseThrow(() -> new RuntimeException("Purchase order not found"));
    }
    
    @Transactional
    public PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrder orderDetails) {
        PurchaseOrder order = getPurchaseOrderById(id);
        order.setExpectedDeliveryDate(orderDetails.getExpectedDeliveryDate());
        order.setNotes(orderDetails.getNotes());
        return purchaseOrderRepository.save(order);
    }
    
    @Transactional
    public void updatePurchaseOrderStatus(Long id, PurchaseOrder.POStatus status) {
        PurchaseOrder order = getPurchaseOrderById(id);
        order.setStatus(status);
        if (status == PurchaseOrder.POStatus.SHIPPED) {
            order.setActualDeliveryDate(java.time.LocalDateTime.now());
        }
        purchaseOrderRepository.save(order);
    }
    
    @Transactional(readOnly = true)
    public Page<PurchaseOrder> searchPurchaseOrders(String keyword, Pageable pageable) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return purchaseOrderRepository.searchPurchaseOrders(ownerId, keyword, pageable);
    }
    
    private String generatePONumber(Long ownerId) {
        String year = String.valueOf(java.time.Year.now().getValue());
        String sequence = String.format("%04d", purchaseOrderRepository.countByOwnerId(ownerId) + 1);
        return "PO-" + year + "-" + sequence;
    }
}
