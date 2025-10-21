package com.monaum.Rapid_Global.module.expenses.expense;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Monaum Hossain
 * @since Oct 21, 2025
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResDto {

    private Long id;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private Long paymentMethodId;
    private String paymentMethodName;
    private String paidTo;
    private LocalDate date;
    private String description;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime approvedAt;
}
