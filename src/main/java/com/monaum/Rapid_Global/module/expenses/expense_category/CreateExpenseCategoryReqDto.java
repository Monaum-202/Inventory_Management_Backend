package com.monaum.Rapid_Global.module.expenses.expense_category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Monaum Hossain
 * @since Oct 21, 2025
 */

@Data
public class CreateExpenseCategoryReqDto {

    @NotBlank(message = "Expense Category name required.")
    private String name;

    private String description;
}