package com.vijay.User_Master.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResultDTO {
    private String testName;
    private Double testPrice;
    private String parameterName;
    private String resultValue;
    private String unit;
    private List<String> referenceRanges;
}
