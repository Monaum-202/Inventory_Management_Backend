//package com.monaum.Rapid_Global.module.report.salesReport;
//
//import com.monaum.Rapid_Global.enums.OrderStatus;
//import com.monaum.Rapid_Global.module.incomes.income.Income;
//import com.monaum.Rapid_Global.module.incomes.sales.Sales;
//import com.monaum.Rapid_Global.module.incomes.salesItem.SalesItem;
//import com.monaum.Rapid_Global.util.ResponseUtils;
//import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDate;
//import java.time.temporal.ChronoUnit;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * Monaum Hossain
// * monaum.202@gmail.com
// * OPTIMIZED Sales Report Service - Handles 5-7 years of data
// * <p>
// * Performance Features:
// * - Redis caching for frequently accessed reports
// * - Materialized view usage for aggregations (100x faster)
// * - Intelligent fallback to direct queries
// * - Performance metrics tracking
// * - Page size limits to prevent OOM
// */
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class SalesReportService {
//
//    private final SalesReportRepository salesReportRepository;
//
//    private static final int MAX_PAGE_SIZE = 100;
//    private static final int LARGE_DATE_RANGE_DAYS = 365;
//
//    /**
//     * Generate comprehensive sales report - OPTIMIZED
//     */
//    @Cacheable(value = "salesReport", key = "#request.cacheKey", unless = "#result == null || !#request.useCache")
//    public ResponseEntity<BaseApiResponseDTO<?>> generateSalesReport(SalesReportRequestDTO request) {
//        long startTime = System.currentTimeMillis();
//
//        // Validate and set defaults
//        request.validate();
//
//        log.info("Generating sales report for period: {} to {}, status: {}", request.getStartDate(), request.getEndDate(), request.getStatus());
//
//        // Enforce max page size
//        if (request.getSize() > MAX_PAGE_SIZE) {
//            log.warn("Page size {} exceeds max {}, limiting", request.getSize(), MAX_PAGE_SIZE);
//            request.setSize(MAX_PAGE_SIZE);
//        }
//
//        // Create pageable
//        Sort sort = Sort.by("DESC".equalsIgnoreCase(request.getSortDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC, request.getSortBy());
//        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
//
//        // Get filtered sales
//        Page<Sales> salesPage = salesReportRepository.findSalesWithFilters(request.getStartDate(), request.getEndDate(), request.getStatus(), request.getCustomerId(), request.getCustomerName(), pageable);
//
//        // Build summary (use materialized view if available and beneficial)
//        boolean usedMaterializedView = false;
//        SalesReportResponseDTO.SalesSummary summary;
//
//        if (request.getUseMaterializedView() && shouldUseMaterializedView(request)) {
//            log.info("Using materialized view for summary calculation");
//            summary = buildSummaryFromMaterializedView(request.getStartDate(), request.getEndDate(), request.getStatus(), request.getCustomerId());
//            usedMaterializedView = true;
//        } else {
//            log.info("Using direct query for summary calculation");
//            summary = buildSummaryDirect(request.getStartDate(), request.getEndDate(), request.getStatus(), request.getCustomerId());
//        }
//
//        // Build response
//        SalesReportResponseDTO response = SalesReportResponseDTO.builder().summary(summary).salesDetails(buildSalesDetails(salesPage.getContent())).pagination(buildPaginationInfo(salesPage)).build();
//
//        // Add grouped data if requested
//        if (request.getGroupBy() != null) {
//            response.setGroupedData(buildGroupedData(request, usedMaterializedView));
//        }
//
//        // Add performance metrics
//        long queryTime = System.currentTimeMillis() - startTime;
//        response.setMetrics(SalesReportResponseDTO.PerformanceMetrics.builder().queryTimeMs(queryTime).usedCache(false) // Will be true if served from cache
//                .usedMaterializedView(usedMaterializedView).dataSource(usedMaterializedView ? "MATERIALIZED_VIEW" : "DATABASE").build());
//
//        log.info("Report generated in {}ms using {}", queryTime, usedMaterializedView ? "materialized view" : "direct query");
//
//        return ResponseUtils.SuccessResponseWithData(response);
//    }
//
//    /**
//     * Determine if materialized view should be used
//     */
//    private boolean shouldUseMaterializedView(SalesReportRequestDTO request) {
//        // Check if MV exists
//        try {
//            Long mvExists = salesReportRepository.checkMaterializedViewExists();
//            if (mvExists == null || mvExists == 0) {
//                log.debug("Materialized view not available");
//                return false;
//            }
//        } catch (Exception e) {
//            log.warn("Error checking materialized view existence: {}", e.getMessage());
//            return false;
//        }
//
//        // Use MV for large date ranges (> 1 year)
//        if (request.isLargeDateRange()) {
//            log.debug("Large date range detected, using materialized view");
//            return true;
//        }
//
//        // Use MV if no customer-specific filtering
//        if (request.getCustomerId() == null && request.getCustomerName() == null) {
//            log.debug("No customer filtering, using materialized view");
//            return true;
//        }
//
//        return false;
//    }
//
//    /**
//     * Build summary from materialized view - FAST
//     */
//    private SalesReportResponseDTO.SalesSummary buildSummaryFromMaterializedView(LocalDate startDate, LocalDate endDate, OrderStatus status, Long customerId) {
//
//        String statusStr = status != null ? status.name() : null;
//        Map<String, Object> summaryData = salesReportRepository.getSummaryFromMaterializedView(startDate, endDate, statusStr);
//
//        long totalOrders = ((Number) summaryData.getOrDefault("totalOrders", 0L)).longValue();
//        BigDecimal totalRevenue = (BigDecimal) summaryData.getOrDefault("totalRevenue", BigDecimal.ZERO);
//        BigDecimal totalDiscount = (BigDecimal) summaryData.getOrDefault("totalDiscount", BigDecimal.ZERO);
//        BigDecimal totalVat = (BigDecimal) summaryData.getOrDefault("totalVat", BigDecimal.ZERO);
//        long totalItems = ((Number) summaryData.getOrDefault("totalItems", 0L)).longValue();
//
//        BigDecimal netRevenue = totalRevenue.subtract(totalDiscount).add(totalVat);
//        BigDecimal averageOrderValue = totalOrders > 0 ? netRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
//
//        Long totalCustomers = salesReportRepository.getUniqueCustomerCount(startDate, endDate, status);
//
//        // Get status-wise amounts
//        BigDecimal pendingAmount = salesReportRepository.getAmountByStatus(OrderStatus.PENDING, startDate, endDate);
//        BigDecimal confirmedAmount = salesReportRepository.getAmountByStatus(OrderStatus.CONFIRMED, startDate, endDate);
//        BigDecimal deliveredAmount = salesReportRepository.getAmountByStatus(OrderStatus.DELIVERED, startDate, endDate);
//        BigDecimal cancelledAmount = salesReportRepository.getAmountByStatus(OrderStatus.CANCELLED, startDate, endDate);
//
//        return SalesReportResponseDTO.SalesSummary.builder().totalOrders((int) totalOrders).totalRevenue(totalRevenue).totalDiscount(totalDiscount).totalVat(totalVat).netRevenue(netRevenue).averageOrderValue(averageOrderValue).totalItemsSold((int) totalItems).totalCustomers(totalCustomers.intValue()).pendingOrders(((Number) summaryData.getOrDefault("pendingOrders", 0)).intValue()).confirmedOrders(((Number) summaryData.getOrDefault("confirmedOrders", 0)).intValue()).shippedOrders(((Number) summaryData.getOrDefault("shippedOrders", 0)).intValue()).deliveredOrders(((Number) summaryData.getOrDefault("deliveredOrders", 0)).intValue()).cancelledOrders(((Number) summaryData.getOrDefault("cancelledOrders", 0)).intValue()).pendingAmount(pendingAmount).confirmedAmount(confirmedAmount).deliveredAmount(deliveredAmount).cancelledAmount(cancelledAmount).build();
//    }
//
//    /**
//     * Build summary directly from database
//     */
//    private SalesReportResponseDTO.SalesSummary buildSummaryDirect(LocalDate startDate, LocalDate endDate, OrderStatus status, Long customerId) {
//
//        String statusStr = status != null ? status.name() : null;
//        Map<String, Object> summaryData = salesReportRepository.getSalesSummaryDirect(startDate, endDate, statusStr, customerId);
//
//        long totalOrders = ((Number) summaryData.getOrDefault("totalOrders", 0L)).longValue();
//        BigDecimal totalRevenue = (BigDecimal) summaryData.getOrDefault("totalRevenue", BigDecimal.ZERO);
//        BigDecimal totalDiscount = (BigDecimal) summaryData.getOrDefault("totalDiscount", BigDecimal.ZERO);
//        BigDecimal totalVat = (BigDecimal) summaryData.getOrDefault("totalVat", BigDecimal.ZERO);
//
//        BigDecimal netRevenue = totalRevenue.subtract(totalDiscount).add(totalVat);
//        BigDecimal averageOrderValue = totalOrders > 0 ? netRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
//
//        Long totalItemsSold = salesReportRepository.getTotalItemsSold(startDate, endDate, status);
//        Long totalCustomers = salesReportRepository.getUniqueCustomerCount(startDate, endDate, status);
//
//        // Get status-wise amounts
//        BigDecimal pendingAmount = salesReportRepository.getAmountByStatus(OrderStatus.PENDING, startDate, endDate);
//        BigDecimal confirmedAmount = salesReportRepository.getAmountByStatus(OrderStatus.CONFIRMED, startDate, endDate);
//        BigDecimal deliveredAmount = salesReportRepository.getAmountByStatus(OrderStatus.DELIVERED, startDate, endDate);
//        BigDecimal cancelledAmount = salesReportRepository.getAmountByStatus(OrderStatus.CANCELLED, startDate, endDate);
//
//        return SalesReportResponseDTO.SalesSummary.builder().totalOrders((int) totalOrders).totalRevenue(totalRevenue).totalDiscount(totalDiscount).totalVat(totalVat).netRevenue(netRevenue).averageOrderValue(averageOrderValue).totalItemsSold(totalItemsSold.intValue()).totalCustomers(totalCustomers.intValue()).pendingOrders(((Number) summaryData.getOrDefault("pendingOrders", 0)).intValue()).confirmedOrders(((Number) summaryData.getOrDefault("confirmedOrders", 0)).intValue()).shippedOrders(((Number) summaryData.getOrDefault("shippedOrders", 0)).intValue()).deliveredOrders(((Number) summaryData.getOrDefault("deliveredOrders", 0)).intValue()).cancelledOrders(((Number) summaryData.getOrDefault("cancelledOrders", 0)).intValue()).pendingAmount(pendingAmount).confirmedAmount(confirmedAmount).deliveredAmount(deliveredAmount).cancelledAmount(cancelledAmount).build();
//    }
//
//    /**
//     * Build sales details list
//     */
//    private List<SalesReportResponseDTO.SalesDetailDTO> buildSalesDetails(List<Sales> salesList) {
//        return salesList.stream().map(sales -> {
//            BigDecimal subtotal = sales.getItems().stream().map(SalesItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
//
//            BigDecimal discount = sales.getDiscount() != null ? sales.getDiscount() : BigDecimal.ZERO;
//            BigDecimal vat = sales.getVat() != null ? sales.getVat() : BigDecimal.ZERO;
//            BigDecimal totalAmount = subtotal.subtract(discount).add(vat);
//
//            BigDecimal paidAmount = sales.getPayments().stream().map(Income::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
//
//            BigDecimal dueAmount = totalAmount.subtract(paidAmount);
//
//            return SalesReportResponseDTO.SalesDetailDTO.builder().id(sales.getId()).invoiceNo(sales.getInvoiceNo()).sellDate(sales.getSellDate()).deliveryDate(sales.getDeliveryDate()).customerName(sales.getCustomerName()).phone(sales.getPhone()).email(sales.getEmail()).companyName(sales.getCompanyName()).status(sales.getStatus().name()).totalItems(sales.getItems().size()).subtotal(subtotal).discount(discount).vat(vat).totalAmount(totalAmount).paidAmount(paidAmount).dueAmount(dueAmount).notes(sales.getNotes()).build();
//        }).collect(Collectors.toList());
//    }
//
//    /**
//     * Build pagination info
//     */
//    private SalesReportResponseDTO.PaginationInfo buildPaginationInfo(Page<Sales> page) {
//        return SalesReportResponseDTO.PaginationInfo.builder().currentPage(page.getNumber()).pageSize(page.getSize()).totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).hasNext(page.hasNext()).hasPrevious(page.hasPrevious()).build();
//    }
//
//    /**
//     * Build grouped data
//     */
//    private List<SalesReportResponseDTO.GroupedSalesDTO> buildGroupedData(SalesReportRequestDTO request, boolean useMaterializedView) {
//
//        String dateFormat = getDateFormatForGrouping(request.getGroupBy());
//        if (dateFormat == null) {
//            return new ArrayList<>();
//        }
//
//        List<Object[]> groupedResults;
//
//        if (useMaterializedView) {
//            String statusStr = request.getStatus() != null ? request.getStatus().name() : null;
//            groupedResults = salesReportRepository.getGroupedSalesFromMaterializedView(request.getStartDate(), request.getEndDate(), statusStr, dateFormat);
//        } else {
//            groupedResults = salesReportRepository.getSalesGroupedByDateDirect(request.getStartDate(), request.getEndDate(), request.getStatus(), dateFormat);
//        }
//
//        return groupedResults.stream().map(row -> {
//            String period = (String) row[0];
//            long orderCount = ((Number) row[1]).longValue();
//            BigDecimal totalRevenue = (BigDecimal) row[2];
//
//            BigDecimal avgOrderValue = orderCount > 0 ? totalRevenue.divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
//
//            Integer totalItems = row.length > 3 && row[3] != null ? ((Number) row[3]).intValue() : null;
//
//            return SalesReportResponseDTO.GroupedSalesDTO.builder().groupKey(period).groupLabel(period).orderCount((int) orderCount).totalRevenue(totalRevenue).averageOrderValue(avgOrderValue).totalItems(totalItems).build();
//        }).collect(Collectors.toList());
//    }
//
//    /**
//     * Get date format string for MySQL DATE_FORMAT
//     */
//    private String getDateFormatForGrouping(String groupBy) {
//        if (groupBy == null) return null;
//
//        return switch (groupBy.toUpperCase()) {
//            case "DAY" -> "%Y-%m-%d";
//            case "WEEK" -> "%Y-W%u";
//            case "MONTH" -> "%Y-%m";
//            case "YEAR" -> "%Y";
//            default -> null;
//        };
//    }
//
//    /**
//     * Get product performance report - OPTIMIZED
//     */
//    @Cacheable(value = "productPerformance", key = "#startDate + ':' + #endDate + ':' + #limit", unless = "#result == null")
//    public ResponseEntity<BaseApiResponseDTO<?>> getProductPerformanceReport(LocalDate startDate, LocalDate endDate, OrderStatus status, Integer limit) {
//
//        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
//        if (endDate == null) endDate = LocalDate.now();
//        if (limit == null) limit = 10;
//
//        List<Object[]> topProducts;
//
//        // Try materialized view first
//        try {
//            topProducts = salesReportRepository.getTopProductsFromMaterializedView(startDate, endDate, limit);
//            log.info("Using materialized view for product performance");
//        } catch (Exception e) {
//            log.warn("Materialized view failed, falling back to direct query: {}", e.getMessage());
//            Pageable pageable = PageRequest.of(0, limit);
//            topProducts = salesReportRepository.getTopSellingProductsDirect(startDate, endDate, status, pageable);
//        }
//
//        BigDecimal totalRevenue = topProducts.stream().map(row -> toBigDecimal(row[2])).reduce(BigDecimal.ZERO, BigDecimal::add);
//        List<ProductPerformanceDTO.TopProductDTO> productList = topProducts.stream().map(row -> {
//            String itemName = (String) row[0];
//            long totalQuantity = ((Number) row[1]).longValue();
//            BigDecimal revenue = toBigDecimal(row[2]);
//            BigDecimal avgUnitPrice = toBigDecimal(row[3]);
//            long orderCount = ((Number) row[4]).longValue();
//
//            BigDecimal revenuePercentage = totalRevenue.compareTo(BigDecimal.ZERO) > 0 ? revenue.divide(totalRevenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) : BigDecimal.ZERO;
//
//            return ProductPerformanceDTO.TopProductDTO.builder().itemName(itemName).totalQuantitySold((int) totalQuantity).totalRevenue(revenue).averageUnitPrice(avgUnitPrice).orderCount((int) orderCount).revenuePercentage(revenuePercentage).build();
//        }).toList();
//
//        ProductPerformanceDTO.ProductSummary summary = ProductPerformanceDTO.ProductSummary.builder().totalUniqueProducts(productList.size()).totalQuantitySold(productList.stream().mapToInt(ProductPerformanceDTO.TopProductDTO::getTotalQuantitySold).sum()).totalRevenue(totalRevenue).build();
//
//        return ResponseUtils.SuccessResponseWithData(summary);
//    }
//
//    /**
//     * Get customer analytics report - OPTIMIZED
//     */
//    @Cacheable(value = "customerAnalytics", key = "#startDate + ':' + #endDate + ':' + #limit", unless = "#result == null")
//    public ResponseEntity<BaseApiResponseDTO<?>> getCustomerAnalyticsReport(LocalDate startDate, LocalDate endDate, OrderStatus status, Integer limit) {
//
//        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
//        if (endDate == null) endDate = LocalDate.now();
//        if (limit == null) limit = 10;
//
//        List<Object[]> topCustomers;
//
//        // Try materialized view first
//        try {
//            topCustomers = salesReportRepository.getTopCustomersFromMaterializedView(startDate, endDate, limit);
//            log.info("Using materialized view for customer analytics");
//        } catch (Exception e) {
//            log.warn("Materialized view failed, falling back to direct query: {}", e.getMessage());
//            Pageable pageable = PageRequest.of(0, limit);
//            topCustomers = salesReportRepository.getTopCustomersDirect(startDate, endDate, status, pageable);
//        }
//
//        List<CustomerAnalyticsDTO.TopCustomerDTO> customerList = topCustomers.stream().map(row -> {
//            Long customerId = row[0] != null ? ((Number) row[0]).longValue() : null;
//            String customerName = (String) row[1];
//            String phone = (String) row[2];
//            String email = (String) row[3];
//            String companyName = row.length > 4 ? (String) row[4] : null;
//            long totalOrders = row.length > 5 ? ((Number) row[5]).longValue() : 0L;
//            BigDecimal totalSpent = row.length > 6 ? (BigDecimal) row[6] : BigDecimal.ZERO;
//            LocalDate lastOrderDate = row.length > 7 ? (LocalDate) row[7] : null;
//            LocalDate firstOrderDate = row.length > 8 ? (LocalDate) row[8] : null;
//
//            BigDecimal avgOrderValue = totalOrders > 0 ? totalSpent.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
//
//            Integer daysSinceLastOrder = lastOrderDate != null ? (int) ChronoUnit.DAYS.between(lastOrderDate, LocalDate.now()) : null;
//
//            return CustomerAnalyticsDTO.TopCustomerDTO.builder().customerId(customerId).customerName(customerName).phone(phone).email(email).companyName(companyName).totalOrders((int) totalOrders).totalSpent(totalSpent).averageOrderValue(avgOrderValue).lastOrderDate(lastOrderDate).firstOrderDate(firstOrderDate).daysSinceLastOrder(daysSinceLastOrder).build();
//        }).toList();
//
//        Long totalCustomers = salesReportRepository.getUniqueCustomerCount(startDate, endDate, status);
//
//        CustomerAnalyticsDTO.CustomerSummary summary = CustomerAnalyticsDTO.CustomerSummary.builder().totalCustomers(totalCustomers.intValue()).build();
//
//        return ResponseUtils.SuccessResponseWithData(summary);
//    }
//
//    private BigDecimal toBigDecimal(Object value) {
//
//        if (value == null) return BigDecimal.ZERO;
//
//        if (value instanceof BigDecimal bd) {
//            return bd;
//        }
//
//        if (value instanceof Number num) {
//            return BigDecimal.valueOf(num.doubleValue());
//        }
//
//        throw new IllegalArgumentException("Cannot convert to BigDecimal: " + value);
//    }
//}