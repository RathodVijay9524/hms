package com.vijay.User_Master.dto.inventory;

import com.vijay.User_Master.entity.Requisition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequisitionKanbanDTO {
    
    private List<Requisition> criticalPending;
    private List<Requisition> routinePending;
    private List<Requisition> processing;
    
    // Manual getters and setters for compatibility
    public List<Requisition> getCriticalPending() { return criticalPending; }
    public void setCriticalPending(List<Requisition> criticalPending) { this.criticalPending = criticalPending; }
    
    public List<Requisition> getRoutinePending() { return routinePending; }
    public void setRoutinePending(List<Requisition> routinePending) { this.routinePending = routinePending; }
    
    public List<Requisition> getProcessing() { return processing; }
    public void setProcessing(List<Requisition> processing) { this.processing = processing; }
}
