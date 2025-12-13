package com.monaum.Rapid_Global.module.report.financialReport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
class CashFlowDto {
    private LocalDate date;
    private Double openingBalance;
    private Double totalIncome;
    private Double totalExpense;
    private Double closingBalance;
    private Double netCashFlow;
}