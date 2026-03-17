package com.monaum.Rapid_Global.module.report.financialReport.expenseReport;

import com.monaum.Rapid_Global.enums.Status;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * Filter parameters for Expense Report.
 */
@Data
public class ExpenseReportFilterDTO {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    private Status status;            // null = all statuses

    private String paidTo;            // searches paidTo + paidToCompany

    private String categoryName;      // searches TransactionCategory.name

    private String employeeName;      // searches Employee display name (optional)
}