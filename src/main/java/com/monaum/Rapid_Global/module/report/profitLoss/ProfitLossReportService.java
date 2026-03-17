package com.monaum.Rapid_Global.module.report.profitLoss;

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
 * lineType is stored as a plain String constant so JRXML expressions
 * can compare with .equals() without inner-type binary name issues.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfitLossReportService {

    // ---- lineType String constants (mirrors old enum names) ----
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
    // MAIN BUILD METHOD
    // ----------------------------------------------------------------

    public ProfitLossReportDTO buildReport(LocalDate dateFrom, LocalDate dateTo) {

        log.debug("P&L buildReport: dateFrom={} dateTo={}", dateFrom, dateTo);

        List<Object[]> incomeRows    = incomeRepo.sumIncomeByCategoryForPL(dateFrom, dateTo);
        List<Object[]> expenseRows   = expenseRepo.sumExpenseByCategoryForPL(dateFrom, dateTo);
        List<Object[]> monthlyIncome  = incomeRepo.monthlyIncomeSumsForPL(dateFrom, dateTo);
        List<Object[]> monthlyExpense = expenseRepo.monthlyExpenseSumsForPL(dateFrom, dateTo);

        List<CategoryBreakdownDTO> incomeCategories  = toCategories(incomeRows);
        List<CategoryBreakdownDTO> expenseCategories = toCategories(expenseRows);

        BigDecimal totalIncome  = sumCategories(incomeCategories);
        BigDecimal totalExpense = sumCategories(expenseCategories);
        BigDecimal netProfit    = totalIncome.subtract(totalExpense);

        applyPercentages(incomeCategories,  totalIncome);
        applyPercentages(expenseCategories, totalExpense);

        BigDecimal netMarginPct = totalIncome.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : netProfit.divide(totalIncome, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        List<MonthlyBreakdownDTO>      monthly   = buildMonthly(monthlyIncome, monthlyExpense);
        List<ProfitLossLineItemDTO>    lineItems = buildLineItems(
                incomeCategories, totalIncome,
                expenseCategories, totalExpense,
                netProfit);

        return ProfitLossReportDTO.builder()
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .generatedAt(LocalDateTime.now().format(GEN_FMT))
                .totalIncome(scale(totalIncome))
                .totalExpense(scale(totalExpense))
                .netProfit(scale(netProfit))
                .netLabel(netProfit.compareTo(BigDecimal.ZERO) >= 0 ? "NET PROFIT" : "NET LOSS")
                .netMarginPct(netMarginPct)
                .incomeByCategory(incomeCategories)
                .expenseByCategory(expenseCategories)
                .monthlyBreakdown(monthly)
                .lineItems(lineItems)
                .build();
    }

    // ----------------------------------------------------------------
    // LINE ITEMS — flat ordered list for JRXML datasource
    // lineType is now a plain String — no inner enum
    // ----------------------------------------------------------------

    private List<ProfitLossLineItemDTO> buildLineItems(
            List<CategoryBreakdownDTO> incomeCategories,  BigDecimal totalIncome,
            List<CategoryBreakdownDTO> expenseCategories, BigDecimal totalExpense,
            BigDecimal netProfit) {

        List<ProfitLossLineItemDTO> items = new ArrayList<>();

        // ── INCOME ───────────────────────────────────────────────────
        items.add(sectionHeader("INCOME"));
        for (CategoryBreakdownDTO c : incomeCategories) {
            items.add(categoryLine(c.getCategoryName(), c.getAmount(), c.getPercentage(), true));
        }
        items.add(subtotal("Total Income", totalIncome, true));
        items.add(spacer());

        // ── EXPENSES ─────────────────────────────────────────────────
        items.add(sectionHeader("EXPENSES"));
        for (CategoryBreakdownDTO c : expenseCategories) {
            items.add(categoryLine(c.getCategoryName(), c.getAmount(), c.getPercentage(), false));
        }
        items.add(subtotal("Total Expenses", totalExpense, false));
        items.add(spacer());

        // ── NET ───────────────────────────────────────────────────────
        boolean isProfit = netProfit.compareTo(BigDecimal.ZERO) >= 0;
        items.add(ProfitLossLineItemDTO.builder()
                .lineType(LT_NET)
                .label(isProfit ? "NET PROFIT" : "NET LOSS")
                .amount(netProfit.abs())
                .positive(isProfit)
                .build());

        return items;
    }

    // ----------------------------------------------------------------
    // MONTHLY TREND
    // ----------------------------------------------------------------

    private List<MonthlyBreakdownDTO> buildMonthly(List<Object[]> incomeRows,
                                                   List<Object[]> expenseRows) {
        Map<Integer, BigDecimal> incomeMap  = toMonthMap(incomeRows);
        Map<Integer, BigDecimal> expenseMap = toMonthMap(expenseRows);

        Set<Integer> allKeys = new TreeSet<>();
        allKeys.addAll(incomeMap.keySet());
        allKeys.addAll(expenseMap.keySet());

        return allKeys.stream().map(key -> {
            int year  = key / 100;
            int month = key % 100;
            BigDecimal inc = incomeMap.getOrDefault(key, BigDecimal.ZERO);
            BigDecimal exp = expenseMap.getOrDefault(key, BigDecimal.ZERO);
            BigDecimal net = inc.subtract(exp);
            String label = LocalDate.of(year, month, 1).format(MONTH_FMT);
            return MonthlyBreakdownDTO.builder()
                    .monthLabel(label).sortKey(key)
                    .totalIncome(scale(inc)).totalExpense(scale(exp)).netProfit(scale(net))
                    .build();
        }).collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // HELPERS
    // ----------------------------------------------------------------

    private List<CategoryBreakdownDTO> toCategories(List<Object[]> rows) {
        return rows.stream().map(r -> CategoryBreakdownDTO.builder()
                .categoryName((String) r[0])
                .amount(scale((BigDecimal) r[1]))
                .percentage(BigDecimal.ZERO)
                .build()).collect(Collectors.toList());
    }

    private void applyPercentages(List<CategoryBreakdownDTO> cats, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) return;
        for (CategoryBreakdownDTO c : cats) {
            c.setPercentage(c.getAmount()
                    .divide(total, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(1, RoundingMode.HALF_UP));
        }
    }

    private BigDecimal sumCategories(List<CategoryBreakdownDTO> cats) {
        return cats.stream().map(CategoryBreakdownDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Map<Integer, BigDecimal> toMonthMap(List<Object[]> rows) {
        Map<Integer, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] r : rows) {
            int year  = ((Number) r[0]).intValue();
            int month = ((Number) r[1]).intValue();
            map.put(year * 100 + month, scale((BigDecimal) r[2]));
        }
        return map;
    }

    // ---- line item factories (all use String constants) ----

    private ProfitLossLineItemDTO sectionHeader(String label) {
        return ProfitLossLineItemDTO.builder()
                .lineType(LT_SECTION_HEADER).label(label).build();
    }

    private ProfitLossLineItemDTO categoryLine(String label, BigDecimal amount,
                                               BigDecimal pct, Boolean positive) {
        return ProfitLossLineItemDTO.builder()
                .lineType(LT_CATEGORY)
                .label(label).amount(amount).percentage(pct).positive(positive).build();
    }

    private ProfitLossLineItemDTO subtotal(String label, BigDecimal amount, Boolean positive) {
        return ProfitLossLineItemDTO.builder()
                .lineType(LT_SUBTOTAL)
                .label(label).amount(amount).positive(positive).build();
    }

    private ProfitLossLineItemDTO spacer() {
        return ProfitLossLineItemDTO.builder()
                .lineType(LT_SPACER).label("").build();
    }

    private BigDecimal scale(BigDecimal v) {
        return (v != null ? v : BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }
}