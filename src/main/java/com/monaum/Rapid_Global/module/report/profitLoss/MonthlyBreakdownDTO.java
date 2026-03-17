package com.monaum.Rapid_Global.module.report.profitLoss;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * One month row in the monthly trend table of the P&L.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyBreakdownDTO {

    /** e.g. "Jan 2025" */
    private String     monthLabel;
    /** numeric sort key — YYYYMM, e.g. 202501 */
    private int        sortKey;

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    /** totalIncome - totalExpense (can be negative) */
    private BigDecimal netProfit;
}