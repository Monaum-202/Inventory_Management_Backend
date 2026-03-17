package com.monaum.Rapid_Global.module.report.expenseReport;

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
 * One row in the Expense Report table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseReportRowDTO {

    private Long          id;
    private String        expenseId;          // human-readable expense reference
    private String        categoryName;       // TransactionCategory.name
    private String        paymentMethod;      // PaymentMethod.name
    private String        transactionId;      // external transaction reference
    private String        paidTo;             // vendor / party name
    private String        paidToCompany;
    private String        employeeName;       // Employee display name (nullable)
    private String        invoiceNo;          // linked Purchase.invoiceNo (nullable)
    private BigDecimal    amount;
    private LocalDate     expenseDate;
    private String        description;
    private Status        status;
    private LocalDateTime approvedAt;
    private String        approvedBy;         // User display name (nullable)
    private String        createdByName;
}