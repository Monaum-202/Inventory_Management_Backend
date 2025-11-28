package com.monaum.Rapid_Global.module.incomes.income;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeReqDTO {

    @NotNull(message = "Income Category is required")
    private Long incomeCategory;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Payment Method is required")
    private Long paymentMethodId;

    private String paidFrom;

    private String paidFromCompany;

    @NotNull(message = "Income date is required")
    private LocalDate incomeDate;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

}
