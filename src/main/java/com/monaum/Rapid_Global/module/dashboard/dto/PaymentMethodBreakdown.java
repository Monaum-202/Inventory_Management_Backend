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
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodBreakdown {
    private String paymentMethodName;
    private BigDecimal amount;
    private Long count;
}
