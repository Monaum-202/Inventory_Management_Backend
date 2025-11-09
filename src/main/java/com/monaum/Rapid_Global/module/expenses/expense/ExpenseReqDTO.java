package com.monaum.Rapid_Global.module.expenses.expense;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 25-Oct-25 10:32 PM
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseReqDTO {

    @NotNull(message = "Expense Category is required")
    private Long expenseCategory;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    private Long paymentMethodId;

    private String paidTo;

    @NotNull(message = "Expense date is required")
    private LocalDate expenseDate;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private Long employeeId;

}
