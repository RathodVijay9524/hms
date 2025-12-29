package com.vijay.User_Master.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineSuggestionDTO {
    private String name;
    private Integer currentStock;
    private String unitOfMeasure;
}
