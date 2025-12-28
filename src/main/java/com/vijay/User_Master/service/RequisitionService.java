package com.vijay.User_Master.service;

import com.vijay.User_Master.Helper.CommonUtils;
import com.vijay.User_Master.dto.inventory.RequisitionKanbanDTO;
import com.vijay.User_Master.entity.Requisition;
import com.vijay.User_Master.entity.User;
import com.vijay.User_Master.repository.RequisitionRepository;
import com.vijay.User_Master.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequisitionService {
    
    private final RequisitionRepository requisitionRepository;
    private final UserRepository userRepository;
    
    // Dashboard Statistics
    @Transactional(readOnly = true)
    public Long getPendingRequisitionsCount() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return requisitionRepository.getPendingRequisitionsCount(ownerId);
    }
    
    @Transactional(readOnly = true)
    public Long getCriticalRequisitionsCount() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return requisitionRepository.getCriticalRequisitionsCount(ownerId);
    }
    
    // Kanban View for Frontend
    @Transactional(readOnly = true)
    public RequisitionKanbanDTO getKanbanView() {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        List<Requisition> criticalPending = requisitionRepository.findCriticalPendingRequisitions(ownerId);
        List<Requisition> routinePending = requisitionRepository.findPendingRequisitions(ownerId).stream()
                .filter(r -> r.getPriority() == Requisition.RequisitionPriority.ROUTINE)
                .collect(Collectors.toList());
        List<Requisition> processing = requisitionRepository.findProcessingRequisitions(ownerId);
        
        return RequisitionKanbanDTO.builder()
                .criticalPending(criticalPending)
                .routinePending(routinePending)
                .processing(processing)
                .build();
    }
    
    // Requisition Management
    @Transactional
    public Requisition createRequisition(Requisition requisition) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        requisition.setOwner(owner);
        return requisitionRepository.save(requisition);
    }
    
    @Transactional(readOnly = true)
    public Page<Requisition> getAllRequisitions(Pageable pageable) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return requisitionRepository.findByOwnerIdAndIsDeletedFalse(ownerId, pageable);
    }
    
    @Transactional(readOnly = true)
    public Requisition getRequisitionById(Long id) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return requisitionRepository.findByIdAndOwnerId(id, ownerId)
                .filter(r -> !r.getIsDeleted())
                .orElseThrow(() -> new RuntimeException("Requisition not found"));
    }
    
    @Transactional
    public Requisition updateRequisition(Long id, Requisition requisitionDetails) {
        Requisition requisition = getRequisitionById(id);
        requisition.setRequestingDepartment(requisitionDetails.getRequestingDepartment());
        requisition.setPriority(requisitionDetails.getPriority());
        requisition.setExpectedDate(requisitionDetails.getExpectedDate());
        requisition.setNotes(requisitionDetails.getNotes());
        return requisitionRepository.save(requisition);
    }
    
    @Transactional
    public void approveRequisition(Long id, Long approvedBy) {
        Requisition requisition = getRequisitionById(id);
        requisition.setStatus(Requisition.RequisitionStatus.APPROVED);
        requisition.setApprovedBy(approvedBy);
        requisition.setApprovedDate(java.time.LocalDateTime.now());
        requisitionRepository.save(requisition);
    }
    
    @Transactional
    public void fulfillRequisition(Long id) {
        Requisition requisition = getRequisitionById(id);
        requisition.setStatus(Requisition.RequisitionStatus.FULFILLED);
        requisition.setFulfilledDate(java.time.LocalDateTime.now());
        requisitionRepository.save(requisition);
    }
    
    @Transactional(readOnly = true)
    public List<Requisition> getRequisitionsByDepartment(String department) {
        Long ownerId = CommonUtils.getLoggedInUser().getOwnerId();
        return requisitionRepository.findByRequestingDepartmentAndOwnerIdAndIsDeletedFalse(department, ownerId);
    }
}
