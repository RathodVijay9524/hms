package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Bill;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillResponse {
    private Long id;
    private String billNumber;
    private Long patientId;
    private String patientName;
    private String patientUhid;
    private Long appointmentId;
    private Bill.BillStatus status;
    private Double totalAmount;
    private Double taxAmount;
    private Double discountAmount;
    private Double netAmount;
    private Double paidAmount;
    private Double balanceAmount;
    private LocalDateTime createdOn;
    private List<BillItemDto> items;
}
