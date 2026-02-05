package com.monaum.Rapid_Global.module.report.salesReport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 04-Feb-26 11:31 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPerformanceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<TopProductDTO> topProducts;
    private ProductSummary summary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProductDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        private String itemName;
        private Integer totalQuantitySold;
        private BigDecimal totalRevenue;
        private BigDecimal averageUnitPrice;
        private Integer orderCount;
        private BigDecimal revenuePercentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSummary implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer totalUniqueProducts;
        private Integer totalQuantitySold;
        private BigDecimal totalRevenue;
    }
}