package com.monaum.Rapid_Global.module.report.incomeReport;

import com.monaum.Rapid_Global.enums.Status;
import com.monaum.Rapid_Global.module.incomes.income.Income;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeReportRepo extends JpaRepository<Income, Long> {

    /**
     * Find incomes with filters for incomeReport
     */
    @Query("SELECT i FROM Income i " +
           "WHERE (:startDate IS NULL OR i.incomeDate >= :startDate) " +
           "AND (:endDate IS NULL OR i.incomeDate <= :endDate) " +
           "AND (:categoryId IS NULL OR i.incomeCategory.id = :categoryId) " +
           "AND (:paymentMethodId IS NULL OR i.paymentMethod.id = :paymentMethodId) " +
           "ORDER BY i.incomeDate DESC, i.id DESC")
    Page<Income> findIncomeReport(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("categoryId") Long categoryId,
            @Param("paymentMethodId") Long paymentMethodId,
            Pageable pageable
    );

    /**
     * Get total income by status
     */
    @Query("SELECT COALESCE(SUM(i.amount), 0.0) FROM Income i " +
           "WHERE i.status = :status " +
           "AND (:startDate IS NULL OR i.incomeDate >= :startDate) " +
           "AND (:endDate IS NULL OR i.incomeDate <= :endDate)")
    Double getTotalByStatus(
            @Param("status") Status status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Get count by status
     */
    @Query("SELECT COUNT(i) FROM Income i " +
           "WHERE i.status = :status " +
           "AND (:startDate IS NULL OR i.incomeDate >= :startDate) " +
           "AND (:endDate IS NULL OR i.incomeDate <= :endDate)")
    Long getCountByStatus(
            @Param("status") Status status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Get breakdown by category
     */
    @Query("SELECT i.incomeCategory.name as categoryName, " +
           "COALESCE(SUM(i.amount), 0.0) as totalAmount, " +
           "COUNT(i) as transactionCount " +
           "FROM Income i " +
           "WHERE i.status = com.monaum.Rapid_Global.enums.Status.APPROVED " +
           "AND (:startDate IS NULL OR i.incomeDate >= :startDate) " +
           "AND (:endDate IS NULL OR i.incomeDate <= :endDate) " +
           "GROUP BY i.incomeCategory.id, i.incomeCategory.name " +
           "ORDER BY totalAmount DESC")
    List<Object[]> getCategoryBreakdown(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Get breakdown by payment method
     */
    @Query("SELECT i.paymentMethod.name as paymentMethodName, " +
           "COALESCE(SUM(i.amount), 0.0) as totalAmount, " +
           "COUNT(i) as transactionCount " +
           "FROM Income i " +
           "WHERE i.status = com.monaum.Rapid_Global.enums.Status.APPROVED " +
           "AND (:startDate IS NULL OR i.incomeDate >= :startDate) " +
           "AND (:endDate IS NULL OR i.incomeDate <= :endDate) " +
           "GROUP BY i.paymentMethod.id, i.paymentMethod.name " +
           "ORDER BY totalAmount DESC")
    List<Object[]> getPaymentMethodBreakdown(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Get daily income trend
     */
    @Query("SELECT i.incomeDate as date, " +
           "COALESCE(SUM(i.amount), 0.0) as totalAmount, " +
           "COUNT(i) as transactionCount " +
           "FROM Income i " +
           "WHERE i.status = com.monaum.Rapid_Global.enums.Status.APPROVED " +
           "AND i.incomeDate BETWEEN :startDate AND :endDate " +
           "AND i.incomeDate BETWEEN :startDate AND :endDate " +
           "GROUP BY i.incomeDate " +
           "ORDER BY i.incomeDate")
    List<Object[]> getDailyIncomeTrend(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}