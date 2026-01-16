package com.monaum.Rapid_Global.module.incomes.income;


import com.monaum.Rapid_Global.enums.Status;
import com.monaum.Rapid_Global.module.dashboard.dto.CategoryBreakdown;
import com.monaum.Rapid_Global.module.dashboard.dto.PaymentMethodBreakdown;
import com.monaum.Rapid_Global.module.dashboard.dto.TrendPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
            "WHERE income_id LIKE CONCAT('INC', SUBSTRING(YEAR(CURDATE()),3,2), '%') " +
            "ORDER BY CAST(SUBSTRING(income_id, 7) AS UNSIGNED) DESC " +
            "LIMIT 1 FOR UPDATE",
            nativeQuery = true)
    String findLastIncomeIdForUpdate();

    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.paidFromId = :customerId")
    Double getTotalTransaction(Long customerId);




    // ============================================
    // DASHBOARD QUERY METHODS
    // ============================================

    /**
     * Calculate total income amount for a date range with specific status
     */
    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i " +
            "WHERE i.incomeDate BETWEEN :startDate AND :endDate " +
            "AND i.status = :status")
    Optional<BigDecimal> sumAmountByDateRangeAndStatus(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") Status status);


    // ============================================
    // DASHBOARD QUERY METHODS - CORRECTED
    // ============================================

    /**
     * Get income breakdown by category
     */
    @Query("SELECT new com.monaum.Rapid_Global.module.dashboard.dto.CategoryBreakdown(" +
            "COALESCE(tc.name, 'Uncategorized'), " +
            "COALESCE(SUM(i.amount), 0.0), " +
            "COUNT(i.id), " +
            "CAST(0.0 AS java.math.BigDecimal)) " +
            "FROM Income i " +
            "LEFT JOIN i.incomeCategory tc " +
            "WHERE i.incomeDate BETWEEN :startDate AND :endDate " +
            "AND i.status = :status " +
            "GROUP BY tc.name")
    List<CategoryBreakdown> findCategoryBreakdown(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") Status status);

    /**
     * Get income breakdown by payment method
     */
    @Query("SELECT new com.monaum.Rapid_Global.module.dashboard.dto.PaymentMethodBreakdown(" +
            "COALESCE(pm.name, 'Unknown'), " +
            "COALESCE(SUM(i.amount), 0.0), " +
            "COUNT(i.id)) " +
            "FROM Income i " +
            "LEFT JOIN i.paymentMethod pm " +
            "WHERE i.incomeDate BETWEEN :startDate AND :endDate " +
            "AND i.status = :status " +
            "GROUP BY pm.name")
    List<PaymentMethodBreakdown> findPaymentMethodBreakdown(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") Status status);

    /**
     * Get daily trend data
     */
    @Query("SELECT new com.monaum.Rapid_Global.module.dashboard.dto.TrendPoint(" +
            "i.incomeDate, " +
            "COALESCE(SUM(i.amount), 0.0), " +
            "COUNT(i.id)) " +
            "FROM Income i " +
            "WHERE i.incomeDate BETWEEN :startDate AND :endDate " +
            "AND i.status = :status " +
            "GROUP BY i.incomeDate " +
            "ORDER BY i.incomeDate")
    List<TrendPoint> findDailyTrend(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") Status status);

}

