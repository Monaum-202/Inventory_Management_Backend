package com.monaum.Rapid_Global.module.expenses.expense_category;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Monaum Fahim
 * @since 24-Oct-25 12:14 PM
 */

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExpenseCategoryMapper {

    ExpenseCategory toEntity(CreateExpenseCategoryReqDto dto);

    void toEntity(UpdateExpenseCategoryReqDto dto, @MappingTarget ExpenseCategory expenseCategory);

    ExpenseCategoryResDto toDto(ExpenseCategory expenseCategory);
}