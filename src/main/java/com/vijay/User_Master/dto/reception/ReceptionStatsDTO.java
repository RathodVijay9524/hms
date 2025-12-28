package com.vijay.User_Master.dto.reception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReceptionStatsDTO {
    private long todayRegistrations;
    private long pendingTokens;
    private long totalAppointments;
    private long expectedTodayCount;
    private long checkedInCount;
    private long noShowCount;
    private long inPremiseVisitors;
    private String avgWaitTime;
}
