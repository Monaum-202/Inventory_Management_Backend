package com.monaum.Rapid_Global.module.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class TrendDataResponse {
    private List<TrendPoint> revenueTrend;
    private List<TrendPoint> expenseTrend;
    private String period;
}
