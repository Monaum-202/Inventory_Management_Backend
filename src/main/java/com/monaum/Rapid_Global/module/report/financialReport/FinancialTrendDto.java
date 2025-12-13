package com.monaum.Rapid_Global.module.report.financialReport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
class FinancialTrendDto {
    private LocalDate date;
    private Double incomeAmount;
    private Double expenseAmount;
    private Double netAmount;
    private Long incomeCount;
    private Long expenseCount;
}