package com.monaum.Rapid_Global.module.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * @since 13-Jan-26 9:26 PM
 */
/**
 * Category Breakdown DTO
 * Used for dashboard analytics
 */
@Data
@Builder
@NoArgsConstructor
public class CategoryBreakdown {
    private String categoryName;
    private BigDecimal amount;
    private Long count;
    private BigDecimal percentage;

    /**
     * Constructor for JPA query projection
     * This is what JPA will call from the @Query
     */
    public CategoryBreakdown(String categoryName, BigDecimal amount, Long count, BigDecimal percentage) {
        this.categoryName = categoryName;
        this.amount = amount;
        this.count = count;
        this.percentage = percentage;
    }
}