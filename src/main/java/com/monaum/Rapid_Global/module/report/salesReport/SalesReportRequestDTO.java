package com.monaum.Rapid_Global.module.report.salesReport;

import com.monaum.Rapid_Global.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 04-Feb-26 11:13 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesReportRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    private OrderStatus status;

    private Long customerId;

    private String customerName;

    private String groupBy; // DAY, WEEK, MONTH, YEAR, CUSTOMER, PRODUCT

    private Integer page = 0;

    private Integer size = 20;

    private String sortBy = "sellDate";

    private String sortDirection = "DESC";

    // Performance optimization flags
    private Boolean useMaterializedView = true; // Use pre-calculated data when available

    private Boolean useCache = true; // Enable Redis caching

    /**
     * Generate cache key for this request
     */
    public String getCacheKey() {
        return String.format("sales_report:%s:%s:%s:%s:%s:%s:%d:%d",
                startDate != null ? startDate.toString() : "null",
                endDate != null ? endDate.toString() : "null",
                status != null ? status.name() : "null",
                customerId != null ? customerId.toString() : "null",
                customerName != null ? customerName : "null",
                groupBy != null ? groupBy : "null",
                page,
                size
        );
    }

    /**
     * Validate and set default values
     */
    public void validate() {
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (size == null || size < 1) {
            size = 20;
        }
        if (size > 100) { // Enforce max page size
            size = 100;
        }
        if (page == null || page < 0) {
            page = 0;
        }
    }

    /**
     * Check if date range is large (>1 year)
     */
    public boolean isLargeDateRange() {
        if (startDate == null || endDate == null) {
            return false;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) > 365;
    }
}