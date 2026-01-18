package com.monaum.Rapid_Global.module.dashboard;

import com.monaum.Rapid_Global.enums.OrderStatus;
import com.monaum.Rapid_Global.enums.Status;
import com.monaum.Rapid_Global.enums.TimePeriod;
import com.monaum.Rapid_Global.module.dashboard.dto.*;
import com.monaum.Rapid_Global.module.expenses.expense.ExpenseRepo;
import com.monaum.Rapid_Global.module.expenses.purchase.PurchaseRepo;
import com.monaum.Rapid_Global.module.incomes.customer.CustomerRepo;
import com.monaum.Rapid_Global.module.incomes.income.IncomeRepo;
import com.monaum.Rapid_Global.module.incomes.sales.SalesRepo;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 12-Jan-26 11:31 PM
 */

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final IncomeRepo incomeRepository;
    private final ExpenseRepo expenseRepository;
    private final SalesRepo salesRepository;
    private final CustomerRepo customerRepository;
    private final PurchaseRepo purchaseRepo;

    /**
     * Get comprehensive dashboard metrics with period comparison
     */
    public ResponseEntity<BaseApiResponseDTO<?>> getDashboardMetrics(
            TimePeriod period,
            LocalDate customStart,
            LocalDate customEnd
    ) {

        DateRange currentRange = calculateDateRange(period, customStart, customEnd);
        DateRange previousRange = calculatePreviousDateRange(currentRange);

        BigDecimal currentRevenue = calculateTotalRevenue(currentRange);
        BigDecimal currentExpenses = calculateTotalExpenses(currentRange);
        BigDecimal currentOrders = calculateTotalOrders(currentRange);
        BigDecimal currentCustomer = calculateTotalCustomers(currentRange);
        BigDecimal currentDue = calculateTotalDue(currentRange);
        BigDecimal currentOwed = calculateTotalOwed(currentRange);

        BigDecimal previousRevenue = calculateTotalRevenue(previousRange);
        BigDecimal previousExpenses = calculateTotalExpenses(previousRange);
        BigDecimal previousOrders = calculateTotalOrders(previousRange);
        BigDecimal previousCustomer = calculateTotalCustomers(previousRange);
        BigDecimal previousDue = calculateTotalDue(previousRange);
        BigDecimal previousOwed = calculateTotalOwed(previousRange);

        BigDecimal revenueChange = calculatePercentageChange(previousRevenue, currentRevenue);
        BigDecimal expenseChange = calculatePercentageChange(previousExpenses, currentExpenses);
        BigDecimal orderChange = calculatePercentageChange(previousOrders, currentOrders);
        BigDecimal customerChange = calculatePercentageChange(previousCustomer, currentCustomer);
        BigDecimal dueChange = calculatePercentageChange(previousDue, currentDue);
        BigDecimal owedChange = calculatePercentageChange(previousOwed, currentOwed);

        BigDecimal currentProfit = currentRevenue.subtract(currentExpenses);
        BigDecimal previousProfit = previousRevenue.subtract(previousExpenses);
        BigDecimal profitChange = calculatePercentageChange(previousProfit, currentProfit);

        BigDecimal profitMargin = BigDecimal.ZERO;
        if (currentRevenue.compareTo(BigDecimal.ZERO) > 0) {
            profitMargin = currentProfit
                    .divide(currentRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        DashboardMetricsResponse metrics = DashboardMetricsResponse.builder()
                .totalRevenue(MetricData.builder()
                        .value(currentRevenue)
                        .formattedValue(formatCurrency(currentRevenue))
                        .change(revenueChange)
                        .formattedChange(formatPercentage(revenueChange))
                        .isPositive(revenueChange.compareTo(BigDecimal.ZERO) >= 0)
                        .build())
                .totalExpenses(MetricData.builder()
                        .value(currentExpenses)
                        .formattedValue(formatCurrency(currentExpenses))
                        .change(expenseChange)
                        .formattedChange(formatPercentage(expenseChange))
                        .isPositive(expenseChange.compareTo(BigDecimal.ZERO) < 0)
                        .build())
                .netProfit(MetricData.builder()
                        .value(currentProfit)
                        .formattedValue(formatCurrency(currentProfit))
                        .change(profitChange)
                        .formattedChange(formatPercentage(profitChange))
                        .isPositive(profitChange.compareTo(BigDecimal.ZERO) >= 0)
                        .build())
                .profitMargin(MetricData.builder()
                        .value(profitMargin)
                        .formattedValue(formatPercentage(profitMargin))
                        .change(BigDecimal.ZERO)
                        .formattedChange("0%")
                        .isPositive(true)
                        .build())
                .totalOrders(MetricData.builder()
                        .value(currentOrders)
                        .formattedValue(formatCurrency(currentOrders))
                        .change(orderChange)
                        .formattedChange(formatPercentage(orderChange))
                        .isPositive(orderChange.compareTo(BigDecimal.ZERO) >= 0)
                        .build())
                .totalCustomers(MetricData.builder()
                        .value(currentCustomer)
                        .formattedValue(formatCurrency(currentCustomer))
                        .change(customerChange)
                        .formattedChange(formatPercentage(customerChange))
                        .isPositive(customerChange.compareTo(BigDecimal.ZERO) >= 0)
                        .build())
                .totalDue(MetricData.builder()
                        .value(currentDue)
                        .formattedValue(formatCurrency(currentDue))
                        .change(dueChange)
                        .formattedChange(formatPercentage(dueChange))
                        .isPositive(expenseChange.compareTo(BigDecimal.ZERO) < 0)
                        .build())
                .totalOwed(MetricData.builder()
                        .value(currentOwed)
                        .formattedValue(formatCurrency(currentOwed))
                        .change(owedChange)
                        .formattedChange(formatPercentage(owedChange))
                        .isPositive(owedChange.compareTo(BigDecimal.ZERO) >= 0)
                        .build())
                .period(period.name())
                .startDate(currentRange.getStartDate())
                .endDate(currentRange.getEndDate())
                .build();

        return ResponseUtils.SuccessResponseWithData(metrics);
    }

    /**
     * Calculate total revenue for a date range
     */
    private BigDecimal calculateTotalRevenue(DateRange range) {
        return incomeRepository.sumAmountByDateRangeAndStatus(
                range.getStartDate(),
                range.getEndDate(),
                Status.APPROVED
        ).orElse(BigDecimal.ZERO);
    }

    /**
     * Calculate total expenses for a date range
     */
    private BigDecimal calculateTotalExpenses(DateRange range) {
        return expenseRepository.sumAmountByDateRangeAndStatus(
                range.getStartDate(),
                range.getEndDate(),
                Status.APPROVED
        ).orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateTotalOrders(DateRange range) {
        return salesRepository.sumAmountByDateRange(
                range.getStartDate(),
                range.getEndDate()
        ).orElse(BigDecimal.ZERO);

    }

    private BigDecimal calculateTotalCustomers(DateRange range) {
        return customerRepository.sumCustomerByDateRange(
                range.getStartDate().atStartOfDay(),
                range.getEndDate().atTime(LocalTime.MAX)
        ).orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateTotalDue(DateRange range) {
        return purchaseRepo.getDueAmountByDateRange(
                range.getStartDate(),
                range.getEndDate()
        );
    }

    private BigDecimal calculateTotalOwed(DateRange range) {
        return salesRepository.getOwedAmountByDateRange(
                range.getStartDate(),
                range.getEndDate()
        );
    }

    /**
     * Get detailed revenue breakdown
     */
    public ResponseEntity<BaseApiResponseDTO<?>> getRevenueDetails(TimePeriod period) {
        DateRange range = calculateDateRange(period, null, null);

        List<CategoryBreakdown> categoryBreakdown = incomeRepository
                .findCategoryBreakdown(range.getStartDate(), range.getEndDate(), Status.APPROVED);

        List<PaymentMethodBreakdown> paymentBreakdown = incomeRepository
                .findPaymentMethodBreakdown(range.getStartDate(), range.getEndDate(), Status.APPROVED);
        RevenueDetailsResponse response = RevenueDetailsResponse.builder()
                .totalRevenue(calculateTotalRevenue(range))
                .categoryBreakdown(categoryBreakdown)
                .paymentMethodBreakdown(paymentBreakdown)
                .period(period.name())
                .build();
        return ResponseUtils.SuccessResponseWithData(response);
    }

    /**
     * Get detailed expense breakdown
     */
    public ResponseEntity<BaseApiResponseDTO<?>> getExpenseDetails(TimePeriod period) {
        DateRange range = calculateDateRange(period, null, null);

        List<CategoryBreakdown> categoryBreakdown = expenseRepository
                .findCategoryBreakdown(range.getStartDate(), range.getEndDate(), Status.APPROVED);

        List<PaymentMethodBreakdown> paymentBreakdown = expenseRepository
                .findPaymentMethodBreakdown(range.getStartDate(), range.getEndDate(), Status.APPROVED);

        ExpenseDetailsResponse response = ExpenseDetailsResponse.builder()
                .totalExpenses(calculateTotalExpenses(range))
                .categoryBreakdown(categoryBreakdown)
                .paymentMethodBreakdown(paymentBreakdown)
                .period(period.name())
                .build();

        return ResponseUtils.SuccessResponseWithData(response);
    }

    /**
     * Get trend data for charts
     */
    public ResponseEntity<BaseApiResponseDTO<?>> getTrendData(TimePeriod period) {
        DateRange range = calculateDateRange(period, null, null);

        List<TrendPoint> revenueTrend = incomeRepository
                .findDailyTrend(range.getStartDate(), range.getEndDate(), Status.APPROVED);

        List<TrendPoint> expenseTrend = expenseRepository
                .findDailyTrend(range.getStartDate(), range.getEndDate(), Status.APPROVED);

        TrendDataResponse response = TrendDataResponse.builder()
                .revenueTrend(revenueTrend)
                .expenseTrend(expenseTrend)
                .period(period.name())
                .build();

        return ResponseUtils.SuccessResponseWithData(response);
    }

    /**
     * Calculate date range based on period
     */
    private DateRange calculateDateRange(TimePeriod period, LocalDate customStart, LocalDate customEnd) {
        if (customStart != null && customEnd != null) {
            return new DateRange(customStart, customEnd);
        }

        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        switch (period) {
            case TODAY:
                startDate = endDate;
                break;
            case WEEK:
                startDate = endDate.minusWeeks(1);
                break;
            case MONTH:
                startDate = endDate.minusMonths(1);
                break;
            case YEAR:
                startDate = endDate.minusYears(1);
                break;
            default:
                startDate = endDate;
        }

        return new DateRange(startDate, endDate);
    }

    /**
     * Calculate previous date range for comparison
     */
    private DateRange calculatePreviousDateRange(DateRange current) {
        long daysBetween = ChronoUnit.DAYS.between(current.getStartDate(), current.getEndDate());

        LocalDate prevEndDate = current.getStartDate().minusDays(1);
        LocalDate prevStartDate = prevEndDate.minusDays(daysBetween);

        return new DateRange(prevStartDate, prevEndDate);
    }

    /**
     * Calculate percentage change between two values
     */
    private BigDecimal calculatePercentageChange(BigDecimal oldValue, BigDecimal newValue) {
        if (oldValue.compareTo(BigDecimal.ZERO) == 0) {
            return newValue.compareTo(BigDecimal.ZERO) > 0 ? new BigDecimal("100") : BigDecimal.ZERO;
        }

        return newValue.subtract(oldValue)
                .divide(oldValue, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * Format currency value
     */
    private String formatCurrency(BigDecimal value) {
        return String.format("%,.2f", value);
    }

    /**
     * Format percentage value
     */
    private String formatPercentage(BigDecimal value) {
        return String.format("%.1f%%", value);
    }
}