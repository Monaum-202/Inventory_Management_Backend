package com.monaum.Rapid_Global.module.report.salesReport.newReport;

import com.monaum.Rapid_Global.enums.OrderStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * Filter parameters for Sales Report
 */
@Data
public class SalesReportFilterDTO {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    private OrderStatus status; // null = all statuses

    private String customerName; // optional keyword filter
}