package com.monaum.Rapid_Global.module.report.financialReport.incomeReport.newReport;

import com.monaum.Rapid_Global.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * One row in the Income Report table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeReportRowDTO {

    private Long          id;
    private String        incomeId;       // human-readable income reference
    private String        categoryName;   // TransactionCategory.name
    private String        paymentMethod;  // PaymentMethod.name
    private String        paidFrom;       // customer / party name
    private String        paidFromCompany;
    private String        invoiceNo;      // linked Sales.invoiceNo (nullable)
    private BigDecimal    amount;
    private LocalDate     incomeDate;
    private String        description;
    private Status        status;
    private LocalDateTime approvedAt;
    private String        approvedBy;     // User display name (nullable)
}