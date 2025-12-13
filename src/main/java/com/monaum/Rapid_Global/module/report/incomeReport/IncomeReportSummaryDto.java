package com.monaum.Rapid_Global.module.report.incomeReport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeReportSummaryDto {
    
    // Total statistics
    private Double totalIncome;
    private Double approvedIncome;
    private Double pendingIncome;
    private Double cancelledIncome;
    
    // Count statistics
    private Long totalTransactions;
    private Long approvedTransactions;
    private Long pendingTransactions;
    private Long cancelledTransactions;
    
    // Average
    private Double averageTransactionAmount;
    
    // Breakdown by category
    private List<CategoryBreakdown> categoryBreakdowns;
    
    // Breakdown by payment method
    private List<PaymentMethodBreakdown> paymentMethodBreakdowns;
    
    // Date range
    private LocalDate startDate;
    private LocalDate endDate;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryBreakdown {
        private String categoryName;
        private Double totalAmount;
        private Long transactionCount;
        private Double percentage;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentMethodBreakdown {
        private String paymentMethodName;
        private Double totalAmount;
        private Long transactionCount;
        private Double percentage;
    }
}