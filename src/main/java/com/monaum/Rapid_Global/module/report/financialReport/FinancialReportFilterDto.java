package com.monaum.Rapid_Global.module.report.financialReport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialReportFilterDto {
    
    private LocalDate startDate;
    private LocalDate endDate;
    private Long categoryId;
    private Long paymentMethodId;
    private String status; // PENDING, APPROVED, CANCELLED
    private String transactionType; // INCOME, EXPENSE, ALL
    private String searchTerm; // Search in paidFrom/paidTo
    private Double minAmount;
    private Double maxAmount;
}

