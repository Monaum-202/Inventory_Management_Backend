package com.monaum.Rapid_Global.module.report.financialReport.expenseReport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * Full payload returned by the Expense Report service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseReportDTO {

    // ---- filter echo ----
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String    statusFilter;

    // ---- summary stats ----
    private int        totalRecords;
    private BigDecimal totalAmount;       // sum of ALL records in filter
    private BigDecimal totalApproved;    // sum where status = APPROVED
    private BigDecimal totalPending;     // sum where status = PENDING

    /** count per status,   e.g. {"PENDING": 4, "APPROVED": 12} */
    private Map<String, Long> countByStatus;

    /** count per category, e.g. {"Office Supplies": 5, "Travel": 3} */
    private Map<String, Long> countByCategory;

    // ---- detail rows ----
    private List<ExpenseReportRowDTO> rows;
}