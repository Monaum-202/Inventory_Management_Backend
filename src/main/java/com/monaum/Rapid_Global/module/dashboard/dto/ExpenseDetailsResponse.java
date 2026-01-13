package com.monaum.Rapid_Global.module.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDetailsResponse {
    private BigDecimal totalExpenses;
    private List<CategoryBreakdown> categoryBreakdown;
    private List<PaymentMethodBreakdown> paymentMethodBreakdown;
    private String period;
}