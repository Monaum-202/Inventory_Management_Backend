package com.monaum.Rapid_Global.module.expenses.expense;

import com.monaum.Rapid_Global.enums.Status;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 25-Oct-25 10:32 PM
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResDto {

    private Long id;
    private String expenseId;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private Long paymentMethodId;
    private String paymentMethodName;
    private Long employeeId;
    private String employeeName;
    private String paidTo;
    private LocalDate date;
    private String description;
    private String cancelReason;
//    private Long approvedById;
    private String approvedByName;
    private LocalDateTime approvedAt;
    private Status status;
    private Long createdBy;
    private String createdByName;
}
