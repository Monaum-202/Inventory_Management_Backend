package com.monaum.Rapid_Global.module.report.purchaseReport;

import com.monaum.Rapid_Global.module.expenses.purchase.Purchase;
import com.monaum.Rapid_Global.module.expenses.purchase.PurchaseRepo;
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
public class PurchaseReportService {

    /** Stay below DB IN-clause limit (Oracle/MySQL default ~1000). */
    private static final int PAYMENT_BATCH_SIZE = 1000;

    private final PurchaseRepo purchaseRepository;

    // ----------------------------------------------------------------
    // FULL REPORT  (Excel / PDF)
    // Loads all matching rows — caller must enforce a sensible date
    // range (e.g. 1 year) before calling this.
    // ----------------------------------------------------------------

    public PurchaseReportDTO buildReport(PurchaseReportFilterDTO filter) {

        log.debug("buildReport: dateFrom={} dateTo={} status={} supplier={}",
                filter.getDateFrom(), filter.getDateTo(),
                filter.getStatus(), filter.getSupplierName());

        // Step 1 — load purchases with items (single query, no payments join)
        List<Purchase> purchaseList = purchaseRepository.fetchPurchasesWithItems(
                filter.getDateFrom(), filter.getDateTo(),
                filter.getStatus(), filter.getSupplierName());

        if (purchaseList.isEmpty()) {
            return emptyReport(filter);
        }

        // Step 2 — batch-fetch paid totals in one aggregated query
        List<Long> ids = purchaseList.stream()
                .map(Purchase::getId)
                .collect(Collectors.toList());
        Map<Long, BigDecimal> paidMap = buildPaidMap(ids);

        // Step 3 — map to DTOs (single pass)
        List<PurchaseReportRowDTO> rows = new ArrayList<>(purchaseList.size());

        BigDecimal totalSub      = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalVat      = BigDecimal.ZERO;
        BigDecimal totalAmount   = BigDecimal.ZERO;
        BigDecimal totalPaid     = BigDecimal.ZERO;
        BigDecimal totalDue      = BigDecimal.ZERO;
        Map<String, Long> countByStatus = new LinkedHashMap<>();

        for (Purchase p : purchaseList) {
            PurchaseReportRowDTO row = toRow(p, paidMap);
            rows.add(row);

            totalSub      = totalSub.add(row.getSubTotal());
            totalDiscount = totalDiscount.add(row.getDiscount());
            totalVat      = totalVat.add(row.getVat());
            totalAmount   = totalAmount.add(row.getTotalAmount());
            totalPaid     = totalPaid.add(row.getPaidAmount());
            totalDue      = totalDue.add(row.getDueAmount());
            countByStatus.merge(row.getStatus().name(), 1L, Long::sum);
        }

        return PurchaseReportDTO.builder()
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

    public Page<PurchaseReportRowDTO> buildReportPage(PurchaseReportFilterDTO filter,
                                                       Pageable pageable) {

        // Step 1 — paginated query (no fetch join)
        Page<Purchase> purchasePage = purchaseRepository.fetchPurchasesPage(
                filter.getDateFrom(), filter.getDateTo(),
                filter.getStatus(), filter.getSupplierName(),
                pageable);

        if (purchasePage.isEmpty()) {
            return Page.empty(pageable);
        }

        // Step 2 — enrich current page with items
        List<Long> pageIds = purchasePage.getContent().stream()
                .map(Purchase::getId)
                .collect(Collectors.toList());

        Map<Long, Purchase> enrichedMap = purchaseRepository.fetchItemsForIds(pageIds)
                .stream()
                .collect(Collectors.toMap(Purchase::getId, Function.identity()));

        // Step 3 — paid totals for this page only
        Map<Long, BigDecimal> paidMap = buildPaidMap(pageIds);

        // Step 4 — map to DTOs preserving original sort order
        List<PurchaseReportRowDTO> rows = purchasePage.getContent().stream()
                .map(p -> toRow(enrichedMap.getOrDefault(p.getId(), p), paidMap))
                .collect(Collectors.toList());

        return new PageImpl<>(rows, pageable, purchasePage.getTotalElements());
    }

    // ----------------------------------------------------------------
    // ENTITY → DTO
    // ----------------------------------------------------------------

    private PurchaseReportRowDTO toRow(Purchase p, Map<Long, BigDecimal> paidMap) {

        BigDecimal subTotal = p.getItems().stream()
                .map(i -> i.getTotalPrice() != null ? i.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = nvl(p.getDiscount());
        BigDecimal vat      = nvl(p.getVat());
        BigDecimal total    = subTotal.subtract(discount).add(vat)
                                      .setScale(2, RoundingMode.HALF_UP);

        BigDecimal paid = paidMap.getOrDefault(p.getId(), BigDecimal.ZERO);
        BigDecimal due  = total.subtract(paid).max(BigDecimal.ZERO);

        return PurchaseReportRowDTO.builder()
                .id(p.getId())
                .invoiceNo(p.getInvoiceNo())
                .supplierName(p.getSupplierName())
                .phone(p.getPhone())
                .purchaseDate(p.getPurchaseDate())
                .deliveryDate(p.getDeliveryDate())
                .itemCount(p.getItems().size())
                .subTotal(scale(subTotal))
                .discount(scale(discount))
                .vat(scale(vat))
                .totalAmount(scale(total))
                .paidAmount(scale(paid))
                .dueAmount(scale(due))
                .status(p.getStatus())
                .build();
    }

    // ----------------------------------------------------------------
    // Batch-fetch paid totals — safe for any list size
    // ----------------------------------------------------------------

    private Map<Long, BigDecimal> buildPaidMap(List<Long> ids) {
        Map<Long, BigDecimal> result = new HashMap<>(ids.size());

        for (int i = 0; i < ids.size(); i += PAYMENT_BATCH_SIZE) {
            List<Long> chunk = ids.subList(i, Math.min(i + PAYMENT_BATCH_SIZE, ids.size()));
            List<Object[]> rows = purchaseRepository.sumPaymentsByIds(chunk);
            for (Object[] row : rows) {
                Long       purchaseId = (Long)       row[0];
                BigDecimal total      = (BigDecimal) row[1];
                result.put(purchaseId, total != null ? total : BigDecimal.ZERO);
            }
        }

        return result;
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private PurchaseReportDTO emptyReport(PurchaseReportFilterDTO filter) {
        return PurchaseReportDTO.builder()
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