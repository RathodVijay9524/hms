package com.vijay.User_Master.dto.lab;

import com.vijay.User_Master.entity.LabCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabTestDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private LabCategory category;
    private Double basePrice;
    private boolean active;
    private List<LabParameterDTO> parameters;
}
