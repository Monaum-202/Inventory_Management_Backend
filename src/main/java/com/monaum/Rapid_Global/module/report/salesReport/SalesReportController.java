package com.monaum.Rapid_Global.module.report.salesReport;

import com.monaum.Rapid_Global.enums.OrderStatus;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * @since 04-Feb-26 11:33 PM
 */
@Slf4j
@RestController
@RequestMapping("/api/sales-reports")
@RequiredArgsConstructor
@Tag(name = "Sales Reports (OPTIMIZED)", description = "Industry-level sales reporting with caching and materialized views")
public class SalesReportController {

    private final SalesReportService salesReportService;

    /**
     * Generate comprehensive sales report - OPTIMIZED
     */
    @GetMapping
    @Operation(summary = "Generate comprehensive sales report (OPTIMIZED)", description = "Get detailed sales report with caching, materialized views, and performance metrics")
    public ResponseEntity<BaseApiResponseDTO<?>> getSalesReport(@Parameter(description = "Start date (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @Parameter(description = "End date (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, @Parameter(description = "Filter by status") @RequestParam(required = false) OrderStatus status, @Parameter(description = "Filter by customer ID") @RequestParam(required = false) Long customerId, @Parameter(description = "Filter by customer name") @RequestParam(required = false) String customerName, @Parameter(description = "Group by: DAY, WEEK, MONTH, YEAR") @RequestParam(required = false) String groupBy, @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") Integer page, @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") Integer size, @Parameter(description = "Sort field") @RequestParam(defaultValue = "sellDate") String sortBy, @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String sortDirection, @Parameter(description = "Use materialized view") @RequestParam(defaultValue = "true") Boolean useMaterializedView, @Parameter(description = "Use cache") @RequestParam(defaultValue = "true") Boolean useCache) {
        SalesReportRequestDTO request = new SalesReportRequestDTO();
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setStatus(status);
        request.setCustomerId(customerId);
        request.setCustomerName(customerName);
        request.setGroupBy(groupBy);
        request.setPage(page);
        request.setSize(size);
        request.setSortBy(sortBy);
        request.setSortDirection(sortDirection);
        request.setUseMaterializedView(useMaterializedView);
        request.setUseCache(useCache);

        return salesReportService.generateSalesReport(request);

    }

    /**
     * POST endpoint for complex filters
     */
    @PostMapping
    @Operation(summary = "Generate sales report with POST")
    public ResponseEntity<BaseApiResponseDTO<?>> getSalesReportPost(@RequestBody SalesReportRequestDTO request) {
        return salesReportService.generateSalesReport(request);
    }

    /**
     * Get product performance - OPTIMIZED
     */
    @GetMapping("/products")
    @Operation(summary = "Get product performance (OPTIMIZED)")
    public ResponseEntity<BaseApiResponseDTO<?>> getProductPerformance(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, @RequestParam(required = false) OrderStatus status, @RequestParam(defaultValue = "10") Integer limit) {
        return salesReportService.getProductPerformanceReport(startDate, endDate, status, limit);
    }

    /**
     * Get customer analytics - OPTIMIZED
     */
    @GetMapping("/customers")
    @Operation(summary = "Get customer analytics (OPTIMIZED)")
    public ResponseEntity<BaseApiResponseDTO<?>> getCustomerAnalytics(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, @RequestParam(required = false) OrderStatus status, @RequestParam(defaultValue = "10") Integer limit) {
        return salesReportService.getCustomerAnalyticsReport(startDate, endDate, status, limit);

    }
}