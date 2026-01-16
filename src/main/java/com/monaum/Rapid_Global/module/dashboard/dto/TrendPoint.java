package com.monaum.Rapid_Global.module.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * @since 13-Jan-26 9:25 PM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class    TrendPoint {
    private LocalDate date;
    private BigDecimal amount;
    private Long count;
}
