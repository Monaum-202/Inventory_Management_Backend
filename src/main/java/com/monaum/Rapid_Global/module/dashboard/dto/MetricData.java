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
public class MetricData {
    private BigDecimal value;
    private String formattedValue;
    private BigDecimal change;
    private String formattedChange;
    private boolean isPositive;
}
