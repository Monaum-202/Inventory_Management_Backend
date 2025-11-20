package com.monaum.Rapid_Global.module.expenses.expense;

import com.monaum.Rapid_Global.module.expenses.expense_category.ExpenseCategory;

import com.monaum.Rapid_Global.module.master.paymentMethod.PaymentMethod;
import com.monaum.Rapid_Global.module.personnel.employee.Employee;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Monaum Hossain
 * @since Oct 21, 2025
 */

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "expenseCategory", ignore = true)
    @Mapping(target = "paymentMethod", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    Expense toEntity(ExpenseReqDTO dto);

//    @Mapping(target = "categoryId", source = "expenseCategory.id")
    @Mapping(target = "categoryName", source = "expenseCategory.name")
//    @Mapping(target = "paymentMethodId", source = "paymentMethod.id")
    @Mapping(target = "paymentMethodName", source = "paymentMethod.name")
    @Mapping(target = "employeeId", source = "employee.employeeId")
    @Mapping(target = "employeeName", source = "employee.name")
//    @Mapping(target = "approvedById", source = "approvedBy.id")
    @Mapping(target = "approvedByName", source = "approvedBy.userName")
    @Mapping(target = "date", source = "expenseDate")
    ExpenseResDto toDto(Expense entity);

    List<ExpenseResDto> toDtoList(List<Expense> expenses);
}

//     * Optional — For partial updates (PATCH support)
//     */
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void updateEntityFromDto(ExpenseReqDTO dto, @MappingTarget Expense entity);
//
//    /**
//     * Utility method (optional) — helps set related entities by ID after fetching them in Service.
//     */
//    default void setRelations(Expense expense, ExpenseCategory category, PaymentMethod paymentMethod, User approvedBy, Employee employee) {
//        if (Objects.nonNull(category)) expense.setExpenseCategory(category);
//        if (Objects.nonNull(paymentMethod)) expense.setPaymentMethod(paymentMethod);
//        if (Objects.nonNull(approvedBy)) expense.setApprovedBy(approvedBy);
//        if (Objects.nonNull(employee)) expense.setEmployee(employee);
//    }


