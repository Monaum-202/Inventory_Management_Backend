package com.monaum.Rapid_Global.module.report.salesReport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * @since 04-Feb-26 11:16 PM
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesReportResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private SalesSummary summary;
    private List<SalesDetailDTO> salesDetails;
    private PaginationInfo pagination;
    private List<GroupedSalesDTO> groupedData;
    private PerformanceMetrics metrics; // Added for monitoring

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesSummary implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer totalOrders;
        private BigDecimal totalRevenue;
        private BigDecimal totalDiscount;
        private BigDecimal totalVat;
        private BigDecimal netRevenue;
        private BigDecimal averageOrderValue;
        private Integer totalItemsSold;
        private Integer totalCustomers;

        // Status breakdown
        private Integer pendingOrders;
        private Integer confirmedOrders;
        private Integer shippedOrders;
        private Integer deliveredOrders;
        private Integer cancelledOrders;

        private BigDecimal pendingAmount;
        private BigDecimal confirmedAmount;
        private BigDecimal deliveredAmount;
        private BigDecimal cancelledAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesDetailDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long id;
        private String invoiceNo;
        private LocalDate sellDate;
        private LocalDate deliveryDate;
        private String customerName;
        private String phone;
        private String email;
        private String companyName;
        private String status;
        private Integer totalItems;
        private BigDecimal subtotal;
        private BigDecimal discount;
        private BigDecimal vat;
        private BigDecimal totalAmount;
        private BigDecimal paidAmount;
        private BigDecimal dueAmount;
        private String notes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupedSalesDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        private String groupKey;
        private String groupLabel;
        private Integer orderCount;
        private BigDecimal totalRevenue;
        private BigDecimal averageOrderValue;
        private Integer totalItems;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer currentPage;
        private Integer pageSize;
        private Long totalElements;
        private Integer totalPages;
        private Boolean hasNext;
        private Boolean hasPrevious;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceMetrics implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long queryTimeMs;
        private Boolean usedCache;
        private Boolean usedMaterializedView;
        private String dataSource; // CACHE, MATERIALIZED_VIEW, DATABASE
    }
}