package com.monaum.Rapid_Global.module.incomes.income;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeRepo extends JpaRepository<Income, Long> {

    @Query("""
    SELECT i FROM Income i
    WHERE 
        LOWER(COALESCE(i.incomeId, '')) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(COALESCE(i.paidFrom, '')) LIKE LOWER(CONCAT('%', :search, '%'))
""")
    Page<Income> search(@Param("search") String search, Pageable pageable);

    @Query(value = "SELECT income_id FROM income " +
            "WHERE income_id LIKE CONCAT('EXP', SUBSTRING(YEAR(CURDATE()),3,2), '%') " +
            "ORDER BY CAST(SUBSTRING(income_id, 6) AS UNSIGNED) DESC " +
            "LIMIT 1 FOR UPDATE", nativeQuery = true)
    String findLastIncomeIdForUpdate();

}
