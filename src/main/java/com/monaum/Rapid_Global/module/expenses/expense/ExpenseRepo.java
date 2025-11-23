package com.monaum.Rapid_Global.module.expenses.expense;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

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

    List<Expense> findByEmployeeId(Long employeeId);
//@Query("SELECT e FROM Expense e WHERE e.employee.id = :employeeId")
//Page<Expense> findByEmployeeId(@Param("employeeId") Long employeeId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.employee.id = :employeeId")
    BigDecimal getTotalLends(Long employeeId);


    //test

    @Query(value = "SELECT expense_id FROM expense " +
            "WHERE expense_id LIKE CONCAT('EXP', SUBSTRING(YEAR(CURDATE()),3,2), '%') " +
            "ORDER BY CAST(SUBSTRING(expense_id, 6) AS UNSIGNED) DESC " +
            "LIMIT 1 FOR UPDATE", nativeQuery = true)
    String findLastExpenseIdForUpdate();


    @Query("SELECT e FROM Expense e WHERE e.employee.id IN :employeeIds ORDER BY e.id DESC")
    Page<Expense> findLast15ByEmployeeIds(@Param("employeeIds") List<Long> employeeIds, Pageable pageable);


    @Query("SELECT e.employee.id, COALESCE(SUM(e.amount), 0) " +
            "FROM Expense e WHERE e.employee.id IN :employeeIds GROUP BY e.employee.id")
    List<Object[]> getTotalLendsByEmployeeIds(List<Long> employeeIds);


    @Query(value = "SELECT expense_id FROM expense ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String findLastExpenseId();

}
