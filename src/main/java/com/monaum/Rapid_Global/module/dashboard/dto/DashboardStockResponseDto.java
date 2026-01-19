package com.monaum.Rapid_Global.module.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 19-Jan-26 9:57 PM
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStockResponseDto {
    private String productName;
    private BigDecimal stockAmount;
}
