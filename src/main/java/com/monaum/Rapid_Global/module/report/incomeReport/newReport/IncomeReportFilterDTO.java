package com.monaum.Rapid_Global.module.report.incomeReport.newReport;

import com.monaum.Rapid_Global.enums.Status;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * Filter parameters for Income Report.
 */
@Data
public class IncomeReportFilterDTO {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    private Status status;           // null = all statuses

    private String paidFrom;         // optional keyword filter on paidFrom / paidFromCompany

    private String categoryName;     // optional keyword filter on category name
}