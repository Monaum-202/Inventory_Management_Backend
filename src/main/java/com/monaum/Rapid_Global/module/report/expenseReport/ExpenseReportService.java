package com.monaum.Rapid_Global.module.report.expenseReport;

import com.monaum.Rapid_Global.enums.Status;
import com.monaum.Rapid_Global.module.expenses.expense.Expense;
import com.monaum.Rapid_Global.module.expenses.expense.ExpenseRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpenseReportService {

    private final ExpenseRepo expenseRepo;

    // ----------------------------------------------------------------
    // FULL REPORT  (Excel / PDF)
    // ----------------------------------------------------------------

    public ExpenseReportDTO buildReport(ExpenseReportFilterDTO filter) {

        log.debug("buildReport: dateFrom={} dateTo={} status={} paidTo={} category={} employee={}",
                filter.getDateFrom(), filter.getDateTo(), filter.getStatus(),
                filter.getPaidTo(), filter.getCategoryName(), filter.getEmployeeName());

        List<Expense> expenses = expenseRepo.fetchExpensesForReport(
                filter.getDateFrom(), filter.getDateTo(),
                filter.getStatus(),
                nullIfBlank(filter.getPaidTo()),
                nullIfBlank(filter.getCategoryName()),
                nullIfBlank(filter.getEmployeeName()));

        if (expenses.isEmpty()) {
            return emptyReport(filter);
        }

        List<ExpenseReportRowDTO> rows = expenses.stream()
                .map(this::toRow)
                .collect(Collectors.toList());

        return buildDTO(filter, rows);
    }

    // ----------------------------------------------------------------
    // PAGINATED REPORT  (JSON API)
    // ----------------------------------------------------------------

    public Page<ExpenseReportRowDTO> buildReportPage(ExpenseReportFilterDTO filter,
                                                      Pageable pageable) {

        Page<Expense> expensePage = expenseRepo.fetchExpensesPage(
                filter.getDateFrom(), filter.getDateTo(),
                filter.getStatus(),
                nullIfBlank(filter.getPaidTo()),
                nullIfBlank(filter.getCategoryName()),
                nullIfBlank(filter.getEmployeeName()),
                pageable);

        if (expensePage.isEmpty()) {
            return Page.empty(pageable);
        }

        // Enrich page with all FK associations in a single query
        List<Long> pageIds = expensePage.getContent().stream()
                .map(Expense::getId).collect(Collectors.toList());

        Map<Long, Expense> enriched = expenseRepo.fetchByIdsWithDetails(pageIds)
                .stream()
                .collect(Collectors.toMap(Expense::getId, Function.identity()));

        List<ExpenseReportRowDTO> rows = expensePage.getContent().stream()
                .map(e -> toRow(enriched.getOrDefault(e.getId(), e)))
                .collect(Collectors.toList());

        return new PageImpl<>(rows, pageable, expensePage.getTotalElements());
    }

    // ----------------------------------------------------------------
    // SUMMARY ONLY  (stat cards — no rows)
    // ----------------------------------------------------------------

    public ExpenseReportDTO buildSummary(ExpenseReportFilterDTO filter) {
        ExpenseReportDTO dto = buildReport(filter);
        dto.setRows(null);
        return dto;
    }

    // ----------------------------------------------------------------
    // ENTITY → DTO
    // ----------------------------------------------------------------

    private ExpenseReportRowDTO toRow(Expense e) {

        // Resolve employee name — swap getFullName() / getName() to match your Employee entity
        String employeeName = null;
        if (e.getEmployee() != null) {
            employeeName = e.getEmployee().getName();
        }

        return ExpenseReportRowDTO.builder()
                .id(e.getId())
                .expenseId(e.getExpenseId())
                .categoryName(e.getExpenseCategory() != null
                        ? e.getExpenseCategory().getName() : "—")
                .paymentMethod(e.getPaymentMethod() != null
                        ? e.getPaymentMethod().getName() : "—")
                .transactionId(e.getTransactionId())
                .paidTo(e.getPaidTo())
                .paidToCompany(e.getPaidToCompany())
                .employeeName(employeeName)
                .invoiceNo(e.getPurchase() != null
                        ? e.getPurchase().getInvoiceNo() : null)
                .amount(scale(nvl(e.getAmount())))
                .expenseDate(e.getExpenseDate())
                .description(e.getDescription())
                .status(e.getStatus())
                .approvedAt(e.getApprovedAt())
                .approvedBy(e.getApprovedBy() != null
                        ? e.getApprovedBy().getFullName() : null)
                .createdByName(e.getCreatedByName())
                .build();
    }

    // ----------------------------------------------------------------
    // Aggregate totals from row list
    // ----------------------------------------------------------------

    private ExpenseReportDTO buildDTO(ExpenseReportFilterDTO filter,
                                       List<ExpenseReportRowDTO> rows) {

        BigDecimal totalAmount   = BigDecimal.ZERO;
        BigDecimal totalApproved = BigDecimal.ZERO;
        BigDecimal totalPending  = BigDecimal.ZERO;
        Map<String, Long> countByStatus   = new LinkedHashMap<>();
        Map<String, Long> countByCategory = new LinkedHashMap<>();

        for (ExpenseReportRowDTO row : rows) {
            BigDecimal amt = nvl(row.getAmount());
            totalAmount = totalAmount.add(amt);

            if (row.getStatus() == Status.APPROVED) totalApproved = totalApproved.add(amt);
            if (row.getStatus() == Status.PENDING)  totalPending  = totalPending.add(amt);

            if (row.getStatus() != null) {
                countByStatus.merge(row.getStatus().name(), 1L, Long::sum);
            }
            String cat = (row.getCategoryName() != null && !row.getCategoryName().equals("—"))
                    ? row.getCategoryName() : "Uncategorised";
            countByCategory.merge(cat, 1L, Long::sum);
        }

        return ExpenseReportDTO.builder()
                .dateFrom(filter.getDateFrom())
                .dateTo(filter.getDateTo())
                .statusFilter(filter.getStatus() != null ? filter.getStatus().name() : "ALL")
                .totalRecords(rows.size())
                .totalAmount(scale(totalAmount))
                .totalApproved(scale(totalApproved))
                .totalPending(scale(totalPending))
                .countByStatus(countByStatus)
                .countByCategory(countByCategory)
                .rows(rows)
                .build();
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private ExpenseReportDTO emptyReport(ExpenseReportFilterDTO filter) {
        return ExpenseReportDTO.builder()
                .dateFrom(filter.getDateFrom())
                .dateTo(filter.getDateTo())
                .statusFilter(filter.getStatus() != null ? filter.getStatus().name() : "ALL")
                .totalRecords(0)
                .totalAmount(BigDecimal.ZERO)
                .totalApproved(BigDecimal.ZERO)
                .totalPending(BigDecimal.ZERO)
                .countByStatus(Collections.emptyMap())
                .countByCategory(Collections.emptyMap())
                .rows(Collections.emptyList())
                .build();
    }

    private BigDecimal nvl(BigDecimal val) {
        return val != null ? val : BigDecimal.ZERO;
    }

    private BigDecimal scale(BigDecimal val) {
        return val.setScale(2, RoundingMode.HALF_UP);
    }

    private String nullIfBlank(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}