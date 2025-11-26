package com.monaum.Rapid_Global.module.incomes.income;

import com.monaum.Rapid_Global.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeResDto {

    private Long id;
    private String expenseId;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private Long paymentMethodId;
    private String paymentMethodName;
    private String paidTo;
    private LocalDate date;
    private String description;
    private String cancelReason;
    private String approvedByName;
    private LocalDateTime approvedAt;
    private Status status;
    private Long createdBy;
}
