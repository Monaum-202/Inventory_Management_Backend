package com.monaum.Rapid_Global.module.report.financialReport.cashflowReport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashFlowMonthlyDTO {

    private String     monthLabel;   // e.g. "Jan 2025"
    private int        sortKey;      // YYYYMM

    private BigDecimal totalInflow;
    private BigDecimal totalOutflow;
    /** totalInflow - totalOutflow */
    private BigDecimal netCashFlow;
    /** Running cumulative balance across months */
    private BigDecimal closingBalance;
}