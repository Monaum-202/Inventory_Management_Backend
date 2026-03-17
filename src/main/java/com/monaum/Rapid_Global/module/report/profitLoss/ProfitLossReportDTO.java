package com.monaum.Rapid_Global.module.report.profitLoss;

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
 * Full Profit & Loss report payload.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfitLossReportDTO {

    // ---- period ----
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String    generatedAt;   // formatted timestamp for PDF header

    // ---- top-level figures (APPROVED entries only) ----
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    /** totalIncome − totalExpense  (negative = loss) */
    private BigDecimal netProfit;
    /** "PROFIT" or "LOSS" */
    private String     netLabel;
    /**
     * Net profit as a percentage of income.
     * 0 when totalIncome is zero to avoid divide-by-zero.
     */
    private BigDecimal netMarginPct;

    // ---- income breakdown by category ----
    private List<CategoryBreakdownDTO> incomeByCategory;

    // ---- expense breakdown by category ----
    private List<CategoryBreakdownDTO> expenseByCategory;

    // ---- month-by-month trend ----
    private List<MonthlyBreakdownDTO> monthlyBreakdown;

    // ---- flat ordered list used as JasperReports datasource ----
    private List<ProfitLossLineItemDTO> lineItems;
}