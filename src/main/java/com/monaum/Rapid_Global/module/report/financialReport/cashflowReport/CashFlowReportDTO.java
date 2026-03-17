package com.monaum.Rapid_Global.module.report.financialReport.cashflowReport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashFlowReportDTO {

    // ---- period ----
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String    generatedAt;

    // ---- summary figures (APPROVED only) ----
    private BigDecimal totalInflow;
    private BigDecimal totalOutflow;
    /** totalInflow - totalOutflow */
    private BigDecimal netCashFlow;
    /** "SURPLUS" or "DEFICIT" */
    private String     netLabel;
    /** netCashFlow / totalInflow × 100, 0 when no inflow */
    private BigDecimal netFlowPct;

    // ---- breakdowns ----
    private List<CashFlowLineItemDTO> inflowByCategory;
    private List<CashFlowLineItemDTO> outflowByCategory;

    // ---- monthly trend ----
    private List<CashFlowMonthlyDTO> monthlyBreakdown;

    // ---- flat list for JRXML datasource ----
    private List<CashFlowLineItemDTO> lineItems;
}