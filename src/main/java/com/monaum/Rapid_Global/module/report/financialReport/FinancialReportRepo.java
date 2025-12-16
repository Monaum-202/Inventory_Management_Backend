package com.monaum.Rapid_Global.module.report.financialReport;

import com.monaum.Rapid_Global.enums.Status;
import com.monaum.Rapid_Global.module.expenses.expense.Expense;
import com.monaum.Rapid_Global.module.expenses.expense.ExpenseRepo;
import com.monaum.Rapid_Global.module.incomes.income.Income;
import com.monaum.Rapid_Global.module.report.incomeReport.IncomeReportRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FinancialReportRepo {

    private final IncomeReportRepo incomeRepo;
    private final ExpenseRepo expenseRepo;

    /**
     * Get combined financial transactions (Income + Expense)
     */
    public Page<FinancialTransactionDto> getFinancialTransactions(
            LocalDate startDate,
            LocalDate endDate,
            Long categoryId,
            Long paymentMethodId,
            Status status,
            String transactionType,
            String searchTerm,
            Double minAmount,
            Double maxAmount,
            Pageable pageable
    ) {
        List<FinancialTransactionDto> allTransactions = new ArrayList<>();

        // Get Income transactions
        if (transactionType == null || "ALL".equals(transactionType) || "INCOME".equals(transactionType)) {
            Page<Income> incomes = incomeRepo.findIncomeReport(
                    startDate, endDate, categoryId, paymentMethodId, Pageable.unpaged()
            );
            
            incomes.getContent().forEach(income -> {
                allTransactions.add(mapIncomeToTransaction(income));
            });
        }

        // Get Expense transactions
        if (transactionType == null || "ALL".equals(transactionType) || "EXPENSE".equals(transactionType)) {
            Page<Expense> expenses = expenseRepo.findExpenseReport(
                    startDate, endDate, categoryId, paymentMethodId,
                    status, searchTerm, minAmount, maxAmount, Pageable.unpaged()
            );
            
            expenses.getContent().forEach(expense -> {
                allTransactions.add(mapExpenseToTransaction(expense));
            });
        }

        // Sort by date descending
        allTransactions.sort(Comparator.comparing(FinancialTransactionDto::getTransactionDate).reversed());

        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allTransactions.size());
        
        List<FinancialTransactionDto> pageContent = allTransactions.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, allTransactions.size());
    }

    /**
     * Get income total by status
     */
    public Double getIncomeTotalByStatus(Status status, LocalDate startDate, LocalDate endDate) {
        return incomeRepo.getTotalByStatus(status, startDate, endDate);
    }

    /**
     * Get expense total by status
     */
    public Double getExpenseTotalByStatus(Status status, LocalDate startDate, LocalDate endDate) {
        return expenseRepo.getTotalByStatus(status, startDate, endDate);
    }

    /**
     * Get income count by status
     */
    public Long getIncomeCountByStatus(Status status, LocalDate startDate, LocalDate endDate) {
        return incomeRepo.getCountByStatus(status, startDate, endDate);
    }

    /**
     * Get expense count by status
     */
    public Long getExpenseCountByStatus(Status status, LocalDate startDate, LocalDate endDate) {
        return expenseRepo.getCountByStatus(status, startDate, endDate);
    }

    /**
     * Get income category breakdown
     */
    public List<Object[]> getIncomeCategoryBreakdown(LocalDate startDate, LocalDate endDate) {
        return incomeRepo.getCategoryBreakdown(startDate, endDate);
    }

    /**
     * Get expense category breakdown
     */
    public List<Object[]> getExpenseCategoryBreakdown(LocalDate startDate, LocalDate endDate) {
        return expenseRepo.getCategoryBreakdown(startDate, endDate);
    }

    /**
     * Get payment method breakdown (combined)
     */
    public List<Object[]> getIncomePaymentMethodBreakdown(LocalDate startDate, LocalDate endDate) {
        return incomeRepo.getPaymentMethodBreakdown(startDate, endDate);
    }

    public List<Object[]> getExpensePaymentMethodBreakdown(LocalDate startDate, LocalDate endDate) {
        return expenseRepo.getPaymentMethodBreakdown(startDate, endDate);
    }

    /**
     * Get daily financial trend
     */
    public List<FinancialTrendDto> getDailyFinancialTrend(LocalDate startDate, LocalDate endDate) {
        List<Object[]> incomeData = incomeRepo.getDailyIncomeTrend(startDate, endDate);
        List<Object[]> expenseData = expenseRepo.getDailyExpenseTrend(startDate, endDate);

        // Merge income and expense data by date
        List<FinancialTrendDto> trends = new ArrayList<>();
        
        // Process all dates in range
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Double incomeAmount = 0.0;
            Long incomeCount = 0L;
            Double expenseAmount = 0.0;
            Long expenseCount = 0L;

            // Find income for this date
            for (Object[] row : incomeData) {
                LocalDate rowDate = (LocalDate) row[0];
                if (rowDate.equals(date)) {
                    incomeAmount = (Double) row[1];
                    incomeCount = (Long) row[2];
                    break;
                }
            }

            // Find expense for this date
            for (Object[] row : expenseData) {
                LocalDate rowDate = (LocalDate) row[0];
                if (rowDate.equals(date)) {
                    expenseAmount = ((Number) row[1]).doubleValue();
                    expenseCount = (Long) row[2];
                    break;
                }
            }

            Double netAmount = incomeAmount - expenseAmount;
            trends.add(new FinancialTrendDto(date, incomeAmount, expenseAmount, netAmount, incomeCount, expenseCount));
        }

        return trends;
    }

    /**
     * Map Income to FinancialTransactionDto
     */
    private FinancialTransactionDto mapIncomeToTransaction(Income income) {
        FinancialTransactionDto dto = new FinancialTransactionDto();
        dto.setId(income.getId());
        dto.setTransactionId(income.getIncomeId());
        dto.setTransactionType("INCOME");
        dto.setCategoryName(income.getIncomeCategory() != null ? income.getIncomeCategory().getName() : null);
        dto.setAmount(income.getAmount());
        dto.setPaymentMethodName(income.getPaymentMethod() != null ? income.getPaymentMethod().getName() : null);
        dto.setCounterparty(income.getPaidFrom());
        dto.setCounterpartyCompany(income.getPaidFromCompany());
        dto.setTransactionDate(income.getIncomeDate());
        dto.setDescription(income.getDescription());
        dto.setStatus(income.getStatus() != null ? income.getStatus().name() : null);
        dto.setApprovedByName(income.getApprovedBy() != null ? income.getApprovedBy().getFullName() : null);
        dto.setCreatedByName(income.getCreatedBy() != null ? income.getCreatedBy().getFullName() : null);
        dto.setSalesInvoiceNo(income.getSales() != null ? income.getSales().getInvoiceNo() : null);
        dto.setCancelReason(income.getCancelReason());
        dto.setCreatedDate(income.getCreatedAt() != null ? income.getCreatedAt().toLocalDate() : null);
        return dto;
    }

    /**
     * Map Expense to FinancialTransactionDto
     */
    private FinancialTransactionDto mapExpenseToTransaction(Expense expense) {
        FinancialTransactionDto dto = new FinancialTransactionDto();
        dto.setId(expense.getId());
        dto.setTransactionId(expense.getExpenseId());
        dto.setTransactionType("EXPENSE");
        dto.setCategoryName(expense.getExpenseCategory() != null ? expense.getExpenseCategory().getName() : null);
        dto.setAmount(BigDecimal.valueOf(expense.getAmount() != null ? expense.getAmount().doubleValue() : 0.0));
        dto.setPaymentMethodName(expense.getPaymentMethod() != null ? expense.getPaymentMethod().getName() : null);
        dto.setCounterparty(expense.getPaidTo());
        dto.setCounterpartyCompany(expense.getPaidToCompany());
        dto.setTransactionDate(expense.getExpenseDate());
        dto.setDescription(expense.getDescription());
        dto.setStatus(expense.getStatus() != null ? expense.getStatus().name() : null);
        dto.setApprovedByName(expense.getApprovedBy() != null ? expense.getApprovedBy().getFullName() : null);
        dto.setCreatedByName(expense.getCreatedByName());
        dto.setSalesInvoiceNo(null); // Expenses don't have sales invoices
        dto.setCancelReason(expense.getCancelReason());
        dto.setCreatedDate(expense.getCreatedAt() != null ? expense.getCreatedAt().toLocalDate() : null);
        return dto;
    }
}