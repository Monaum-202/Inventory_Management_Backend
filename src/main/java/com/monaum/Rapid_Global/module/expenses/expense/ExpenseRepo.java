package com.monaum.Rapid_Global.module.expenses.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * @since 25-Oct-25 10:54 PM
 */

@Repository
public interface ExpenseRepo extends JpaRepository<Expense, Long> {
}
