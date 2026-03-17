package com.monaum.Rapid_Global.module.report.financialReport.incomeReport.newReport;

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
 * Full payload returned by the Income Report service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeReportDTO {

    // ---- filter echo ----
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String    statusFilter;

    // ---- summary stats ----
    private int        totalRecords;
    private BigDecimal totalAmount;        // sum of ALL records in filter
    private BigDecimal totalApproved;     // sum where status = APPROVED
    private BigDecimal totalPending;      // sum where status = PENDING

    /** count per status label, e.g. {"PENDING": 4, "APPROVED": 12} */
    private Map<String, Long> countByStatus;

    /** count per category label, e.g. {"Sales Payment": 10, "Advance": 3} */
    private Map<String, Long> countByCategory;

    // ---- detail rows ----
    private List<IncomeReportRowDTO> rows;
}