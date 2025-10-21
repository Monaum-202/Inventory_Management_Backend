package com.monaum.Rapid_Global.module.expenses.expense;

import org.mapstruct.*;
import java.util.List;

/**
 * Monaum Hossain
 * @since Oct 21, 2025
 */

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExpenseMapper {

    @Mapping(source = "expenseCategory.id", target = "categoryId")
    @Mapping(source = "expenseCategory.name", target = "categoryName")
    @Mapping(source = "paymentMethod.id", target = "paymentMethodId")
    @Mapping(source = "paymentMethod.name", target = "paymentMethodName")
    @Mapping(source = "approvedBy.id", target = "approvedById")
    @Mapping(source = "approvedBy.firstName", target = "approvedByName") // adjust as needed
    ExpenseResDto toDto(Expense expense);

    List<ExpenseResDto> toDtoList(List<Expense> expenses);
}
