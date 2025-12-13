package com.monaum.Rapid_Global.module.report.financialReport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialReportSummaryDto {
    
    // Income Statistics
    private Double totalIncome;
    private Double approvedIncome;
    private Double pendingIncome;
    private Double cancelledIncome;
    private Long totalIncomeTransactions;
    private Long approvedIncomeTransactions;
    
    // Expense Statistics
    private Double totalExpense;
    private Double approvedExpense;
    private Double pendingExpense;
    private Double cancelledExpense;
    private Long totalExpenseTransactions;
    private Long approvedExpenseTransactions;
    
    // Net Statistics
    private Double netProfit; // Approved Income - Approved Expense
    private Double netProfitPercentage; // (Net Profit / Approved Income) * 100
    private Double totalCashFlow; // All approved transactions
    
    // Breakdown by category
    private List<CategoryBreakdown> incomeCategories;
    private List<CategoryBreakdown> expenseCategories;
    
    // Breakdown by payment method
    private List<PaymentMethodBreakdown> paymentMethodBreakdowns;
    
    // Top counterparties
    private List<CounterpartyBreakdown> topIncomeCounterparties;
    private List<CounterpartyBreakdown> topExpenseCounterparties;
    
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
        private Double incomeAmount;
        private Double expenseAmount;
        private Double netAmount;
        private Long transactionCount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CounterpartyBreakdown {
        private String counterpartyName;
        private String counterpartyCompany;
        private Double totalAmount;
        private Long transactionCount;
    }
}