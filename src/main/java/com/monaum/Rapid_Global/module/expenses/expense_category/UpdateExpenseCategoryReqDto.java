package com.monaum.Rapid_Global.module.expenses.expense_category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Data
public class UpdateExpenseCategoryReqDto {

    @NotNull(message = "Expense Category ID is required.")
    private Long id;

    @NotBlank(message = "Expense Category name required.")
    private String name;

    private String description;
}
