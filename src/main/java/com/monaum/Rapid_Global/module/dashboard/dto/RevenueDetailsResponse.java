package com.monaum.Rapid_Global.module.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * @since 13-Jan-26 9:26 PM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueDetailsResponse {
    private BigDecimal totalRevenue;
    private List<CategoryBreakdown> categoryBreakdown;
    private List<PaymentMethodBreakdown> paymentMethodBreakdown;
    private String period;
}
