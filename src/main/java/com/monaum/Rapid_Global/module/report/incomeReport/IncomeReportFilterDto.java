package com.monaum.Rapid_Global.module.report.incomeReport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for income incomeReport filters
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeReportFilterDto {
    
    private LocalDate startDate;
    private LocalDate endDate;
    private Long categoryId;
    private Long paymentMethodId;
    private String status; // PENDING, APPROVED, CANCELLED
    private String paidFrom; // Customer name
    private Double minAmount;
    private Double maxAmount;
    private Long salesId; // Filter by specific sale
}

