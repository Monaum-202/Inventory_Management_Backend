package com.monaum.Rapid_Global.module.expenses.expense;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * @since 25-Oct-25 10:54 PM
 */

@Repository
public interface ExpenseRepo extends JpaRepository<Expense, Long> {

    @Query("""
        SELECT e FROM Expense e
        WHERE 
            (e.expenseId) LIKE CONCAT('%', :search, '%')
        """)
    Page<Expense> search(@Param("search") String search, Pageable pageable);
}
