package com.monaum.Rapid_Global.module.report.profitLoss;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * One category line in the Income or Expense section of the P&L.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryBreakdownDTO {

    private String     categoryName;
    private BigDecimal amount;
    /** 0–100, pre-calculated: amount / sectionTotal × 100 */
    private BigDecimal percentage;
}