package com.monaum.Rapid_Global.module.dashboard;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.enums.TimePeriod;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 12-Jan-26 11:30 PM
 */

@RestApiController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Get main dashboard metrics
     * @param period Time period filter (TODAY, WEEK, MONTH, YEAR)
     * @param startDate Optional custom start date
     * @param endDate Optional custom end date
     * @return Dashboard metrics with period comparison
     */
    @GetMapping("/metrics")
    public ResponseEntity<BaseApiResponseDTO<?>> getDashboardMetrics(
            @RequestParam(defaultValue = "TODAY") TimePeriod period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return  dashboardService.getDashboardMetrics(period, startDate, endDate);
    }

    /**
     * Get detailed revenue breakdown
     */
    @GetMapping("/revenue-details")
    public ResponseEntity<BaseApiResponseDTO<?>> getRevenueDetails(
            @RequestParam(defaultValue = "MONTH") TimePeriod period) {

        return dashboardService.getRevenueDetails(period);
        }

    /**
     * Get detailed expense breakdown
     */
    @GetMapping("/expense-details")
    public ResponseEntity<BaseApiResponseDTO<?>> getExpenseDetails(
            @RequestParam(defaultValue = "MONTH") TimePeriod period) {

        return dashboardService.getExpenseDetails(period);
    }

    /**
     * Get trend data for charts
     */
    @GetMapping("/trends")
    public ResponseEntity<BaseApiResponseDTO<?>> getTrendData(
            @RequestParam(defaultValue = "MONTH") TimePeriod period) {

        return dashboardService.getTrendData(period);
    }
}