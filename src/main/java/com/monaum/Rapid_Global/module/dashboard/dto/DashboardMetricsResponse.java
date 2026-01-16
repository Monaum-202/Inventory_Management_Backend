package com.monaum.Rapid_Global.module.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Dashboard Response DTOs
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetricsResponse {
    private MetricData totalRevenue;
    private MetricData totalExpenses;
    private MetricData netProfit;
    private MetricData profitMargin;
    private MetricData totalOrders;
    private String period;
    private LocalDate startDate;
    private LocalDate endDate;
}

