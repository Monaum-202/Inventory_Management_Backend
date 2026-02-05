package com.monaum.Rapid_Global.module.report.salesReport;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 04-Feb-26 11:31 PM
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerAnalyticsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<TopCustomerDTO> topCustomers;
    private CustomerSummary summary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopCustomerDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long customerId;
        private String customerName;
        private String phone;
        private String email;
        private String companyName;
        private Integer totalOrders;
        private BigDecimal totalSpent;
        private BigDecimal averageOrderValue;
        private LocalDate lastOrderDate;
        private LocalDate firstOrderDate;
        private Integer daysSinceLastOrder;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerSummary implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer totalCustomers;
        private Integer newCustomers;
        private Integer returningCustomers;
        private BigDecimal averageLifetimeValue;
        private Double averageOrderFrequency;
    }
}
