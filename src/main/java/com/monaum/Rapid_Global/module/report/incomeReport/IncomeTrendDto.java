package com.monaum.Rapid_Global.module.report.incomeReport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
class IncomeTrendDto {
    private LocalDate date;
    private Double totalAmount;
    private Long transactionCount;
}