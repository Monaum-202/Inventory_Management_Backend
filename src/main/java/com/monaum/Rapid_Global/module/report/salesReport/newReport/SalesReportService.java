package com.monaum.Rapid_Global.module.report.salesReport.newReport;


import com.monaum.Rapid_Global.module.incomes.sales.Sales;
import com.monaum.Rapid_Global.module.incomes.sales.SalesRepo;
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
public class SalesReportService {

    private static final int PAYMENT_BATCH_SIZE = 1000; // stay below DB IN-clause limit

    private final SalesRepo salesRepository;

    // ----------------------------------------------------------------
    // FULL REPORT  (Excel / PDF)
    // Loads all matching rows — caller must enforce a sensible date
    // range (e.g. 1 year) before calling this.
    // ----------------------------------------------------------------

    public SalesReportDTO buildReport(SalesReportFilterDTO filter) {

        log.debug("buildReport: dateFrom={} dateTo={} status={} customer={}",
                filter.getDateFrom(), filter.getDateTo(),
                filter.getStatus(), filter.getCustomerName());

        // Step 1 — load sales with items (single query, no payments join)
        List<Sales> salesList = salesRepository.fetchSalesWithItems(
                filter.getDateFrom(), filter.getDateTo(),
                filter.getStatus(), filter.getCustomerName());

        if (salesList.isEmpty()) {
            return emptyReport(filter);
        }

        // Step 2 — collect IDs, then batch-fetch paid totals in one aggregated query
        List<Long> ids = salesList.stream().map(Sales::getId).collect(Collectors.toList());
        Map<Long, BigDecimal> paidMap = buildPaidMap(ids);

        // Step 3 — map to DTOs (single pass)
        List<SalesReportRowDTO> rows = new ArrayList<>(salesList.size());

        BigDecimal totalSub      = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalVat      = BigDecimal.ZERO;
        BigDecimal totalAmount   = BigDecimal.ZERO;
        BigDecimal totalPaid     = BigDecimal.ZERO;
        BigDecimal totalDue      = BigDecimal.ZERO;
        Map<String, Long> countByStatus = new LinkedHashMap<>();

        for (Sales s : salesList) {
            SalesReportRowDTO row = toRow(s, paidMap);
            rows.add(row);

            // accumulate in the same loop — one pass only
            totalSub      = totalSub.add(row.getSubTotal());
            totalDiscount = totalDiscount.add(row.getDiscount());
            totalVat      = totalVat.add(row.getVat());
            totalAmount   = totalAmount.add(row.getTotalAmount());
            totalPaid     = totalPaid.add(row.getPaidAmount());
            totalDue      = totalDue.add(row.getDueAmount());
            countByStatus.merge(row.getStatus().name(), 1L, Long::sum);
        }

        return SalesReportDTO.builder()
                .dateFrom(filter.getDateFrom())
                .dateTo(filter.getDateTo())
                .statusFilter(filter.getStatus() != null ? filter.getStatus().name() : "ALL")
                .totalOrders(rows.size())
                .totalSubAmount(scale(totalSub))
                .totalDiscount(scale(totalDiscount))
                .totalVat(scale(totalVat))
                .totalAmount(scale(totalAmount))
                .totalPaid(scale(totalPaid))
                .totalDue(scale(totalDue))
                .countByStatus(countByStatus)
                .rows(rows)
                .build();
    }

    // ----------------------------------------------------------------
    // PAGINATED REPORT  (JSON API)
    // ----------------------------------------------------------------

    public Page<SalesReportRowDTO> buildReportPage(SalesReportFilterDTO filter, Pageable pageable) {

        // Step 1 — paginated ID query (no fetch join — avoids HHH-90003004)
        Page<Sales> salesPage = salesRepository.fetchSalesPage(
                filter.getDateFrom(), filter.getDateTo(),
                filter.getStatus(), filter.getCustomerName(),
                pageable);

        if (salesPage.isEmpty()) {
            return Page.empty(pageable);
        }

        // Step 2 — enrich current page with items
        List<Long> pageIds = salesPage.getContent().stream()
                .map(Sales::getId)
                .collect(Collectors.toList());

        // Build a map from the enriched list so we can look up items per sale
        Map<Long, Sales> enrichedMap = salesRepository.fetchItemsForIds(pageIds)
                .stream()
                .collect(Collectors.toMap(Sales::getId, Function.identity()));

        // Step 3 — paid totals for this page only
        Map<Long, BigDecimal> paidMap = buildPaidMap(pageIds);

        // Step 4 — map to DTOs preserving original sort order
        List<SalesReportRowDTO> rows = salesPage.getContent().stream()
                .map(s -> toRow(enrichedMap.getOrDefault(s.getId(), s), paidMap))
                .collect(Collectors.toList());

        return new PageImpl<>(rows, pageable, salesPage.getTotalElements());
    }

    // ----------------------------------------------------------------
    // ENTITY → DTO
    // ----------------------------------------------------------------

    private SalesReportRowDTO toRow(Sales s, Map<Long, BigDecimal> paidMap) {

        BigDecimal subTotal = s.getItems().stream()
                .map(i -> i.getTotalPrice() != null ? i.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = nvl(s.getDiscount());
        BigDecimal vat      = nvl(s.getVat());
        BigDecimal total    = subTotal.subtract(discount).add(vat).setScale(2, RoundingMode.HALF_UP);

        BigDecimal paid = paidMap.getOrDefault(s.getId(), BigDecimal.ZERO);
        BigDecimal due  = total.subtract(paid).max(BigDecimal.ZERO);

        return SalesReportRowDTO.builder()
                .id(s.getId())
                .invoiceNo(s.getInvoiceNo())
                .customerName(s.getCustomerName())
                .phone(s.getPhone())
                .sellDate(s.getSellDate())
                .deliveryDate(s.getDeliveryDate())
                .itemCount(s.getItems().size())
                .subTotal(scale(subTotal))
                .discount(scale(discount))
                .vat(scale(vat))
                .totalAmount(scale(total))
                .paidAmount(scale(paid))
                .dueAmount(scale(due))
                .status(s.getStatus())
                .build();
    }

    // ----------------------------------------------------------------
    // Batch-fetch paid totals — safe for any list size
    // Uses PAYMENT_BATCH_SIZE to stay within DB IN-clause limits
    // ----------------------------------------------------------------

    private Map<Long, BigDecimal> buildPaidMap(List<Long> ids) {
        Map<Long, BigDecimal> result = new HashMap<>(ids.size());

        // Partition into chunks to avoid "IN clause too large" errors on Oracle/MySQL
        for (int i = 0; i < ids.size(); i += PAYMENT_BATCH_SIZE) {
            List<Long> chunk = ids.subList(i, Math.min(i + PAYMENT_BATCH_SIZE, ids.size()));
            List<Object[]> rows = salesRepository.sumPaymentsByIds(chunk);
            for (Object[] row : rows) {
                Long     salesId = (Long)       row[0];
                BigDecimal total = (BigDecimal) row[1];
                result.put(salesId, total != null ? total : BigDecimal.ZERO);
            }
        }

        return result;
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private SalesReportDTO emptyReport(SalesReportFilterDTO filter) {
        return SalesReportDTO.builder()
                .dateFrom(filter.getDateFrom())
                .dateTo(filter.getDateTo())
                .statusFilter(filter.getStatus() != null ? filter.getStatus().name() : "ALL")
                .totalOrders(0)
                .totalSubAmount(BigDecimal.ZERO)
                .totalDiscount(BigDecimal.ZERO)
                .totalVat(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .totalPaid(BigDecimal.ZERO)
                .totalDue(BigDecimal.ZERO)
                .countByStatus(Collections.emptyMap())
                .rows(Collections.emptyList())
                .build();
    }

    private BigDecimal nvl(BigDecimal val) {
        return val != null ? val : BigDecimal.ZERO;
    }

    private BigDecimal scale(BigDecimal val) {
        return val.setScale(2, RoundingMode.HALF_UP);
    }
}