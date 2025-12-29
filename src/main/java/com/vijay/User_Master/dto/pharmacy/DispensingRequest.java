package com.vijay.User_Master.dto.pharmacy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DispensingRequest {
    private Long prescriptionId;
    private String notes;
    private List<DispensingItemRequest> items;
}
