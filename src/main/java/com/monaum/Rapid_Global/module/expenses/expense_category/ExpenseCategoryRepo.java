package com.monaum.Rapid_Global.module.expenses.expense_category;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Monaum Hossain
 * @since Oct 21, 2025
 */

@Repository
public interface ExpenseCategoryRepo extends JpaRepository<ExpenseCategory,Long> {
}
