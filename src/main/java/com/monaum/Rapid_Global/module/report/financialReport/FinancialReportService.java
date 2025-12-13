package com.monaum.Rapid_Global.module.report.financialReport;

import com.monaum.Rapid_Global.enums.Status;
import com.monaum.Rapid_Global.util.PaginationUtil;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import com.monaum.Rapid_Global.util.response.CustomPageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinancialReportService {

    private final FinancialReportRepo financialReportRepo;

    /**
     * Generate combined financial report
     */
    public ResponseEntity<BaseApiResponseDTO<?>> generateReport(
            FinancialReportFilterDto filters,
            Pageable pageable
    ) {
        Status status = filters.getStatus() != null 
            ? Status.valueOf(filters.getStatus().toUpperCase()) 
            : null;

        Page<FinancialTransactionDto> transactionPage = financialReportRepo.getFinancialTransactions(
                filters.getStartDate(),
                filters.getEndDate(),
                filters.getCategoryId(),
                filters.getPaymentMethodId(),
                status,
                filters.getTransactionType(),
                filters.getSearchTerm(),
                filters.getMinAmount(),
                filters.getMaxAmount(),
                pageable
        );

        CustomPageResponseDTO<FinancialTransactionDto> paginatedResponse = 
                PaginationUtil.buildPageResponse(transactionPage, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    /**
     * Get comprehensive financial summary
     */
    public ResponseEntity<BaseApiResponseDTO<?>> getFinancialSummary(
            LocalDate startDate,
            LocalDate endDate
    ) {
        FinancialReportSummaryDto summary = new FinancialReportSummaryDto();
        
        summary.setStartDate(startDate);
        summary.setEndDate(endDate);

        // ========== INCOME STATISTICS ==========
        Double approvedIncome = financialReportRepo.getIncomeTotalByStatus(Status.APPROVED, startDate, endDate);
        Double pendingIncome = financialReportRepo.getIncomeTotalByStatus(Status.PENDING, startDate, endDate);
        Double cancelledIncome = financialReportRepo.getIncomeTotalByStatus(Status.CANCELED, startDate, endDate);
        
        summary.setApprovedIncome(approvedIncome);
        summary.setPendingIncome(pendingIncome);
        summary.setCancelledIncome(cancelledIncome);
        summary.setTotalIncome(approvedIncome + pendingIncome + cancelledIncome);

        Long approvedIncomeCount = financialReportRepo.getIncomeCountByStatus(Status.APPROVED, startDate, endDate);
        Long pendingIncomeCount = financialReportRepo.getIncomeCountByStatus(Status.PENDING, startDate, endDate);
        Long cancelledIncomeCount = financialReportRepo.getIncomeCountByStatus(Status.CANCELED, startDate, endDate);
        
        summary.setApprovedIncomeTransactions(approvedIncomeCount);
        summary.setTotalIncomeTransactions(approvedIncomeCount + pendingIncomeCount + cancelledIncomeCount);

        // ========== EXPENSE STATISTICS ==========
        Double approvedExpense = financialReportRepo.getExpenseTotalByStatus(Status.APPROVED, startDate, endDate);
        Double pendingExpense = financialReportRepo.getExpenseTotalByStatus(Status.PENDING, startDate, endDate);
        Double cancelledExpense = financialReportRepo.getExpenseTotalByStatus(Status.CANCELED, startDate, endDate);
        
        summary.setApprovedExpense(approvedExpense);
        summary.setPendingExpense(pendingExpense);
        summary.setCancelledExpense(cancelledExpense);
        summary.setTotalExpense(approvedExpense + pendingExpense + cancelledExpense);

        Long approvedExpenseCount = financialReportRepo.getExpenseCountByStatus(Status.APPROVED, startDate, endDate);
        Long pendingExpenseCount = financialReportRepo.getExpenseCountByStatus(Status.PENDING, startDate, endDate);
        Long cancelledExpenseCount = financialReportRepo.getExpenseCountByStatus(Status.CANCELED, startDate, endDate);
        
        summary.setApprovedExpenseTransactions(approvedExpenseCount);
        summary.setTotalExpenseTransactions(approvedExpenseCount + pendingExpenseCount + cancelledExpenseCount);

        // ========== NET STATISTICS ==========
        Double netProfit = approvedIncome - approvedExpense;
        summary.setNetProfit(netProfit);
        
        if (approvedIncome > 0) {
            summary.setNetProfitPercentage((netProfit / approvedIncome) * 100);
        } else {
            summary.setNetProfitPercentage(0.0);
        }
        
        summary.setTotalCashFlow(approvedIncome + approvedExpense);

        // ========== INCOME CATEGORY BREAKDOWN ==========
        List<Object[]> incomeCatData = financialReportRepo.getIncomeCategoryBreakdown(startDate, endDate);
        List<FinancialReportSummaryDto.CategoryBreakdown> incomeCategories = new ArrayList<>();
        
        for (Object[] row : incomeCatData) {
            String categoryName = (String) row[0];
            Double totalAmount = (Double) row[1];
            Long count = (Long) row[2];
            Double percentage = approvedIncome > 0 ? (totalAmount / approvedIncome) * 100 : 0.0;
            
            incomeCategories.add(new FinancialReportSummaryDto.CategoryBreakdown(
                categoryName, totalAmount, count, percentage
            ));
        }
        summary.setIncomeCategories(incomeCategories);

        // ========== EXPENSE CATEGORY BREAKDOWN ==========
        List<Object[]> expenseCatData = financialReportRepo.getExpenseCategoryBreakdown(startDate, endDate);
        List<FinancialReportSummaryDto.CategoryBreakdown> expenseCategories = new ArrayList<>();
        
        for (Object[] row : expenseCatData) {
            String categoryName = (String) row[0];
            Double totalAmount = ((Number) row[1]).doubleValue();
            Long count = (Long) row[2];
            Double percentage = approvedExpense > 0 ? (totalAmount / approvedExpense) * 100 : 0.0;
            
            expenseCategories.add(new FinancialReportSummaryDto.CategoryBreakdown(
                categoryName, totalAmount, count, percentage
            ));
        }
        summary.setExpenseCategories(expenseCategories);

        // ========== PAYMENT METHOD BREAKDOWN ==========
        List<Object[]> incomePaymentData = financialReportRepo.getIncomePaymentMethodBreakdown(startDate, endDate);
        List<Object[]> expensePaymentData = financialReportRepo.getExpensePaymentMethodBreakdown(startDate, endDate);
        
        Map<String, FinancialReportSummaryDto.PaymentMethodBreakdown> paymentMap = new HashMap<>();
        
        // Process income payments
        for (Object[] row : incomePaymentData) {
            String methodName = (String) row[0];
            Double amount = (Double) row[1];
            Long count = (Long) row[2];
            
            paymentMap.put(methodName, new FinancialReportSummaryDto.PaymentMethodBreakdown(
                methodName, amount, 0.0, amount, count
            ));
        }
        
        // Process expense payments
        for (Object[] row : expensePaymentData) {
            String methodName = (String) row[0];
            Double amount = ((Number) row[1]).doubleValue();
            Long count = (Long) row[2];
            
            FinancialReportSummaryDto.PaymentMethodBreakdown breakdown = paymentMap.get(methodName);
            if (breakdown != null) {
                breakdown.setExpenseAmount(amount);
                breakdown.setNetAmount(breakdown.getIncomeAmount() - amount);
                breakdown.setTransactionCount(breakdown.getTransactionCount() + count);
            } else {
                paymentMap.put(methodName, new FinancialReportSummaryDto.PaymentMethodBreakdown(
                    methodName, 0.0, amount, -amount, count
                ));
            }
        }
        
        summary.setPaymentMethodBreakdowns(new ArrayList<>(paymentMap.values()));

        return ResponseUtils.SuccessResponseWithData(summary);
    }

    /**
     * Get financial trend (daily)
     */
    public ResponseEntity<BaseApiResponseDTO<?>> getFinancialTrend(
            LocalDate startDate,
            LocalDate endDate
    ) {
        List<FinancialTrendDto> trends = financialReportRepo.getDailyFinancialTrend(startDate, endDate);
        return ResponseUtils.SuccessResponseWithData(trends);
    }

    /**
     * Export financial report
     */
    public List<FinancialTransactionDto> exportReport(FinancialReportFilterDto filters) {
        Status status = filters.getStatus() != null 
            ? Status.valueOf(filters.getStatus().toUpperCase()) 
            : null;

        Page<FinancialTransactionDto> transactionPage = financialReportRepo.getFinancialTransactions(
                filters.getStartDate(),
                filters.getEndDate(),
                filters.getCategoryId(),
                filters.getPaymentMethodId(),
                status,
                filters.getTransactionType(),
                filters.getSearchTerm(),
                filters.getMinAmount(),
                filters.getMaxAmount(),
                Pageable.unpaged()
        );

        return transactionPage.getContent();
    }
}