package com.monaum.Rapid_Global.module.report.financialReport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialTransactionDto {
    
    private Long id;
    private String transactionId; // incomeId or expenseId
    private String transactionType; // INCOME or EXPENSE
    private String categoryName;
    private Double amount;
    private String paymentMethodName;
    private String counterparty; // paidFrom for income, paidTo for expense
    private String counterpartyCompany;
    private LocalDate transactionDate;
    private String description;
    private String status;
    private String approvedByName;
    private String createdByName;
    private String salesInvoiceNo; // Only for income
    private String cancelReason;
    private LocalDate createdDate;
}
