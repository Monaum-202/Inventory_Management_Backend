package com.monaum.Rapid_Global.module.report.financialReport.cashflowReport;

import com.monaum.Rapid_Global.module.expenses.expense.ExpenseRepo;
import com.monaum.Rapid_Global.module.incomes.income.IncomeRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * Builds a Cash Flow Statement from APPROVED Income (inflows)
 * and APPROVED Expense (outflows) within the requested period.
 *
 * Reuses the four P&L repo queries already on IncomeRepo / ExpenseRepo.
 * No new DB queries required.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CashFlowReportService {

    // ---- lineType String constants (no inner enum — avoids JasperReports compile error) ----
    public static final String LT_SECTION_HEADER = "SECTION_HEADER";
    public static final String LT_CATEGORY        = "CATEGORY";
    public static final String LT_SUBTOTAL        = "SUBTOTAL";
    public static final String LT_SPACER          = "SPACER";
    public static final String LT_NET             = "NET";

    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("MMM yyyy");
    private static final DateTimeFormatter GEN_FMT   = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private final IncomeRepo  incomeRepo;
    private final ExpenseRepo expenseRepo;

    // ----------------------------------------------------------------

    public CashFlowReportDTO buildReport(LocalDate dateFrom, LocalDate dateTo) {

        log.debug("CashFlow buildReport: dateFrom={} dateTo={}", dateFrom, dateTo);

        // Reuse the same queries added for the P&L report
        List<Object[]> inflowRows  = incomeRepo.sumIncomeByCategoryForPL(dateFrom, dateTo);
        List<Object[]> outflowRows = expenseRepo.sumExpenseByCategoryForPL(dateFrom, dateTo);
        List<Object[]> monthlyIn   = incomeRepo.monthlyIncomeSumsForPL(dateFrom, dateTo);
        List<Object[]> monthlyOut  = expenseRepo.monthlyExpenseSumsForPL(dateFrom, dateTo);

        // Category breakdowns as line items
        List<CashFlowLineItemDTO> inflowCategories  = toLineItems(inflowRows,  true);
        List<CashFlowLineItemDTO> outflowCategories = toLineItems(outflowRows, false);

        BigDecimal totalInflow  = sum(inflowCategories);
        BigDecimal totalOutflow = sum(outflowCategories);
        BigDecimal netCashFlow  = totalInflow.subtract(totalOutflow);

        applyPercentages(inflowCategories,  totalInflow);
        applyPercentages(outflowCategories, totalOutflow);

        BigDecimal netFlowPct = totalInflow.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : netCashFlow.divide(totalInflow, 4, RoundingMode.HALF_UP)
                             .multiply(BigDecimal.valueOf(100))
                             .setScale(2, RoundingMode.HALF_UP);

        List<CashFlowMonthlyDTO>    monthly   = buildMonthly(monthlyIn, monthlyOut);
        List<CashFlowLineItemDTO>   lineItems = buildLineItems(
                inflowCategories, totalInflow, outflowCategories, totalOutflow, netCashFlow);

        return CashFlowReportDTO.builder()
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .generatedAt(LocalDateTime.now().format(GEN_FMT))
                .totalInflow(scale(totalInflow))
                .totalOutflow(scale(totalOutflow))
                .netCashFlow(scale(netCashFlow))
                .netLabel(netCashFlow.compareTo(BigDecimal.ZERO) >= 0 ? "NET SURPLUS" : "NET DEFICIT")
                .netFlowPct(netFlowPct)
                .inflowByCategory(inflowCategories)
                .outflowByCategory(outflowCategories)
                .monthlyBreakdown(monthly)
                .lineItems(lineItems)
                .build();
    }

    // ----------------------------------------------------------------
    // LINE ITEMS for JRXML
    // ----------------------------------------------------------------

    private List<CashFlowLineItemDTO> buildLineItems(
            List<CashFlowLineItemDTO> inflows,  BigDecimal totalIn,
            List<CashFlowLineItemDTO> outflows, BigDecimal totalOut,
            BigDecimal net) {

        List<CashFlowLineItemDTO> items = new ArrayList<>();

        items.add(sectionHeader("CASH INFLOWS (Income)"));
        items.addAll(inflows);
        items.add(subtotal("Total Inflows", totalIn, true));
        items.add(spacer());

        items.add(sectionHeader("CASH OUTFLOWS (Expenses)"));
        items.addAll(outflows);
        items.add(subtotal("Total Outflows", totalOut, false));
        items.add(spacer());

        boolean surplus = net.compareTo(BigDecimal.ZERO) >= 0;
        items.add(CashFlowLineItemDTO.builder()
                .lineType(LT_NET)
                .label(surplus ? "NET CASH SURPLUS" : "NET CASH DEFICIT")
                .amount(net.abs())
                .positive(surplus)
                .build());

        return items;
    }

    // ----------------------------------------------------------------
    // MONTHLY TREND with running closing balance
    // ----------------------------------------------------------------

    private List<CashFlowMonthlyDTO> buildMonthly(List<Object[]> inRows,
                                                    List<Object[]> outRows) {
        Map<Integer, BigDecimal> inMap  = toMonthMap(inRows);
        Map<Integer, BigDecimal> outMap = toMonthMap(outRows);

        Set<Integer> keys = new TreeSet<>();
        keys.addAll(inMap.keySet());
        keys.addAll(outMap.keySet());

        BigDecimal running = BigDecimal.ZERO;
        List<CashFlowMonthlyDTO> result = new ArrayList<>();

        for (int key : keys) {
            int year  = key / 100;
            int month = key % 100;
            BigDecimal inc = inMap.getOrDefault(key, BigDecimal.ZERO);
            BigDecimal out = outMap.getOrDefault(key, BigDecimal.ZERO);
            BigDecimal net = inc.subtract(out);
            running = running.add(net);

            result.add(CashFlowMonthlyDTO.builder()
                    .monthLabel(LocalDate.of(year, month, 1).format(MONTH_FMT))
                    .sortKey(key)
                    .totalInflow(scale(inc))
                    .totalOutflow(scale(out))
                    .netCashFlow(scale(net))
                    .closingBalance(scale(running))
                    .build());
        }
        return result;
    }

    // ----------------------------------------------------------------
    // HELPERS
    // ----------------------------------------------------------------

    private List<CashFlowLineItemDTO> toLineItems(List<Object[]> rows, boolean positive) {
        return rows.stream().map(r -> CashFlowLineItemDTO.builder()
                .lineType(LT_CATEGORY)
                .label((String) r[0])
                .amount(scale((BigDecimal) r[1]))
                .percentage(BigDecimal.ZERO)
                .positive(positive)
                .build()).collect(Collectors.toList());
    }

    private void applyPercentages(List<CashFlowLineItemDTO> items, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) return;
        for (CashFlowLineItemDTO c : items) {
            c.setPercentage(c.getAmount()
                    .divide(total, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(1, RoundingMode.HALF_UP));
        }
    }

    private BigDecimal sum(List<CashFlowLineItemDTO> items) {
        return items.stream().map(i -> i.getAmount() != null ? i.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Map<Integer, BigDecimal> toMonthMap(List<Object[]> rows) {
        Map<Integer, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] r : rows) {
            int y = ((Number) r[0]).intValue();
            int m = ((Number) r[1]).intValue();
            map.put(y * 100 + m, scale((BigDecimal) r[2]));
        }
        return map;
    }

    private CashFlowLineItemDTO sectionHeader(String label) {
        return CashFlowLineItemDTO.builder().lineType(LT_SECTION_HEADER).label(label).build();
    }

    private CashFlowLineItemDTO subtotal(String label, BigDecimal amount, Boolean positive) {
        return CashFlowLineItemDTO.builder()
                .lineType(LT_SUBTOTAL).label(label).amount(amount).positive(positive).build();
    }

    private CashFlowLineItemDTO spacer() {
        return CashFlowLineItemDTO.builder().lineType(LT_SPACER).label("").build();
    }

    private BigDecimal scale(BigDecimal v) {
        return (v != null ? v : BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }
}