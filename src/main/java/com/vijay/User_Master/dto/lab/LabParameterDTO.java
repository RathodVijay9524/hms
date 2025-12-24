package com.vijay.User_Master.dto.lab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabParameterDTO {
    private Long id;
    private String name;
    private String unit;
    private String method;
    private List<LabReferenceRangeDTO> referenceRanges;
}
