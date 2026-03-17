package com.monaum.Rapid_Global.module.report.financialReport.incomeReport.newReport;

import com.monaum.Rapid_Global.enums.Status;
import com.monaum.Rapid_Global.module.incomes.income.Income;
import com.monaum.Rapid_Global.module.incomes.income.IncomeRepo;
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
 *
 * Income is a flat entity (no items collection), so this service is
 * simpler than the Sales/Purchase equivalents — no batch payment
 * enrichment needed; all data lives directly on Income.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IncomeReportService {

    private final IncomeRepo incomeRepo;

    // ----------------------------------------------------------------
    // FULL REPORT  (Excel / PDF)
    // ----------------------------------------------------------------

    public IncomeReportDTO buildReport(IncomeReportFilterDTO filter) {

        log.debug("buildReport: dateFrom={} dateTo={} status={} paidFrom={} category={}",
                filter.getDateFrom(), filter.getDateTo(), filter.getStatus(),
                filter.getPaidFrom(), filter.getCategoryName());

        List<Income> incomes = incomeRepo.fetchIncomesForReport(
                filter.getDateFrom(), filter.getDateTo(),
                filter.getStatus(),
                nullIfBlank(filter.getPaidFrom()),
                nullIfBlank(filter.getCategoryName()));

        if (incomes.isEmpty()) {
            return emptyReport(filter);
        }

        List<IncomeReportRowDTO> rows = incomes.stream()
                .map(this::toRow)
                .collect(Collectors.toList());

        return buildDTO(filter, rows);
    }

    // ----------------------------------------------------------------
    // PAGINATED REPORT  (JSON API)
    // ----------------------------------------------------------------

    public Page<IncomeReportRowDTO> buildReportPage(IncomeReportFilterDTO filter,
                                                     Pageable pageable) {

        Page<Income> incomePage = incomeRepo.fetchIncomesPage(
                filter.getDateFrom(), filter.getDateTo(),
                filter.getStatus(),
                nullIfBlank(filter.getPaidFrom()),
                nullIfBlank(filter.getCategoryName()),
                pageable);

        if (incomePage.isEmpty()) {
            return Page.empty(pageable);
        }

        // Enrich this page's IDs with associations (avoids N+1)
        List<Long> pageIds = incomePage.getContent().stream()
                .map(Income::getId).collect(Collectors.toList());

        Map<Long, Income> enriched = incomeRepo.fetchByIdsWithDetails(pageIds)
                .stream()
                .collect(Collectors.toMap(Income::getId, Function.identity()));

        List<IncomeReportRowDTO> rows = incomePage.getContent().stream()
                .map(i -> toRow(enriched.getOrDefault(i.getId(), i)))
                .collect(Collectors.toList());

        return new PageImpl<>(rows, pageable, incomePage.getTotalElements());
    }

    // ----------------------------------------------------------------
    // SUMMARY ONLY  (stat cards — no rows)
    // ----------------------------------------------------------------

    public IncomeReportDTO buildSummary(IncomeReportFilterDTO filter) {
        // Reuse buildReport but strip rows before returning
        IncomeReportDTO dto = buildReport(filter);
        dto.setRows(null);
        return dto;
    }

    // ----------------------------------------------------------------
    // ENTITY → DTO
    // ----------------------------------------------------------------

    private IncomeReportRowDTO toRow(Income i) {
        return IncomeReportRowDTO.builder()
                .id(i.getId())
                .incomeId(i.getIncomeId())
                .categoryName(i.getIncomeCategory() != null
                        ? i.getIncomeCategory().getName() : "—")
                .paymentMethod(i.getPaymentMethod() != null
                        ? i.getPaymentMethod().getName() : "—")
                .paidFrom(i.getPaidFrom())
                .paidFromCompany(i.getPaidFromCompany())
                .invoiceNo(i.getSales() != null
                        ? i.getSales().getInvoiceNo() : null)
                .amount(scale(nvl(i.getAmount())))
                .incomeDate(i.getIncomeDate())
                .description(i.getDescription())
                .status(i.getStatus())
                .approvedAt(i.getApprovedAt())
                .approvedBy(i.getApprovedBy() != null
                        ? i.getApprovedBy().getFullName() : null)
                .build();
    }

    // ----------------------------------------------------------------
    // Aggregate totals from row list
    // ----------------------------------------------------------------

    private IncomeReportDTO buildDTO(IncomeReportFilterDTO filter,
                                      List<IncomeReportRowDTO> rows) {

        BigDecimal totalAmount   = BigDecimal.ZERO;
        BigDecimal totalApproved = BigDecimal.ZERO;
        BigDecimal totalPending  = BigDecimal.ZERO;
        Map<String, Long> countByStatus   = new LinkedHashMap<>();
        Map<String, Long> countByCategory = new LinkedHashMap<>();

        for (IncomeReportRowDTO row : rows) {
            BigDecimal amt = nvl(row.getAmount());
            totalAmount = totalAmount.add(amt);

            if (row.getStatus() == Status.APPROVED) totalApproved = totalApproved.add(amt);
            if (row.getStatus() == Status.PENDING)  totalPending  = totalPending.add(amt);

            if (row.getStatus() != null) {
                countByStatus.merge(row.getStatus().name(), 1L, Long::sum);
            }
            String cat = row.getCategoryName() != null ? row.getCategoryName() : "Uncategorised";
            countByCategory.merge(cat, 1L, Long::sum);
        }

        return IncomeReportDTO.builder()
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

    private IncomeReportDTO emptyReport(IncomeReportFilterDTO filter) {
        return IncomeReportDTO.builder()
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

    /** Convert blank/whitespace strings to null so JPQL IS NULL works. */
    private String nullIfBlank(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}