package com.monaum.Rapid_Global.module.report.salesReport.newReport;

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
 * Full payload returned by the report service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesReportDTO {

    // ---- filter echo ----
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String statusFilter;

    // ---- summary stats ----
    private int totalOrders;
    private BigDecimal totalSubAmount;
    private BigDecimal totalDiscount;
    private BigDecimal totalVat;
    private BigDecimal totalAmount;
    private BigDecimal totalPaid;
    private BigDecimal totalDue;

    /** count per status label, e.g. {"PENDING": 4, "COMPLETED": 12} */
    private Map<String, Long> countByStatus;

    // ---- detail rows ----
    private List<SalesReportRowDTO> rows;
}