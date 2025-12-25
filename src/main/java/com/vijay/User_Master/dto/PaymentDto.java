package com.vijay.User_Master.dto;

import com.vijay.User_Master.entity.Payment;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
    private Long id;
    private Long billId;
    private Double amount;
    private Payment.PaymentMode mode;
    private String transactionReference;
    private LocalDateTime paymentDate;
}
