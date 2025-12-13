package com.monaum.Rapid_Global.module.report.incomeReport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for income incomeReport response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeReportResDto {
    
    private String incomeId;
    private String categoryName;
    private Double amount;
    private String paymentMethodName;
    private String paidFrom;
    private String paidFromCompany;
    private LocalDate incomeDate;
    private String description;
    private String status;
    private String approvedByName;
    private String createdByName;
    private String salesInvoiceNo;
    private String cancelReason;
}