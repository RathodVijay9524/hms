package com.vijay.User_Master.dto.lab;

import com.vijay.User_Master.entity.LabOrder.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabOrderDTO {
    private Long id;
    private String orderNumber;
    private Long patientId;
    private List<Long> testIds;
    private List<LabTestDTO> tests; // For response
    private OrderStatus status;
    private LocalDateTime collectionDate;
    private String aiSummary;
    private boolean doctorVerified;
    private String doctorRemarks;
}
