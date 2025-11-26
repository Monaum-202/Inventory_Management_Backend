package com.monaum.Rapid_Global.module.incomes.income;

import com.monaum.Rapid_Global.module.expenses.expense.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeRepo extends JpaRepository<Income, Long> {
}
