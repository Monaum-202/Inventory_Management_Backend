package com.monaum.Rapid_Global.module.incomes.sales;

import com.monaum.Rapid_Global.enums.OrderStatus;
import com.monaum.Rapid_Global.enums.Status;
import com.monaum.Rapid_Global.module.incomes.income.Income;
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
public interface SalesRepo extends JpaRepository<Sales, Long> {

    @Query("""
                SELECT s FROM Sales s
                WHERE 
                    LOWER(COALESCE(s.invoiceNo, '')) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(COALESCE(s.phone, '')) LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<Sales> search(@Param("search") String search, Pageable pageable);


    @Query(value = "SELECT invoice_no FROM SALES " + "WHERE invoice_no LIKE CONCAT('INv', SUBSTRING(YEAR(CURDATE()),3,2), '%') " + "ORDER BY CAST(SUBSTRING(invoice_no, 7) AS UNSIGNED) DESC " + "LIMIT 1 FOR UPDATE", nativeQuery = true)
    String findLastInvoiceNoForUpdate();

    @Query("""
                SELECT 
                  (SUM(i.totalPrice) 
                   - COALESCE(s.discount, 0)
                   + (SUM(i.totalPrice) * (COALESCE(s.vat, 0) / 100))
                  )
                FROM Sales s
                JOIN s.items i
                WHERE s.customerId = :customerId
                GROUP BY s.id
            """)
    List<BigDecimal> calculatePerSaleTotalsByCustomer(Long customerId);

    //dashboard
    @Query("SELECT COUNT(s) FROM Sales s " + "WHERE s.sellDate BETWEEN :startDate AND :endDate ")
    Optional<BigDecimal> sumAmountByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("""
                SELECT 
                  COALESCE(SUM(si.totalPrice), 0)
                  -
                  COALESCE(SUM(i.amount), 0)
                FROM Sales s
                JOIN s.items si
                LEFT JOIN s.payments i
                       ON i.status = com.monaum.Rapid_Global.enums.Status.APPROVED
                       AND i.incomeDate BETWEEN :fromDate AND :toDate
                WHERE s.sellDate BETWEEN :fromDate AND :toDate
            """)
    BigDecimal getOwedAmountByDateRange(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);


//    Report

    // ----------------------------------------------------------------
    // 1. Main sales query — fetches items only (no payments join)
    //    Used by: SalesReportService for export (Excel / PDF)
    // ----------------------------------------------------------------
    @Query("""
            SELECT s FROM Sales s
            LEFT JOIN FETCH s.items
            WHERE (:dateFrom  IS NULL OR s.sellDate       >= :dateFrom)
              AND (:dateTo    IS NULL OR s.sellDate       <= :dateTo)
              AND (:status    IS NULL OR s.status          = :status)
              AND (:customer  IS NULL OR LOWER(s.customerName)
                                        LIKE LOWER(CONCAT('%', :customer, '%')))
            ORDER BY s.sellDate DESC, s.id DESC
            """)
    List<Sales> fetchSalesWithItems(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo")   LocalDate dateTo,
            @Param("status")   OrderStatus status,
            @Param("customer") String customer
    );

    // ----------------------------------------------------------------
    // 2. Paginated ID-only query (no fetch join — just IDs + sort)
    //    Used by: JSON endpoint so the caller gets one page at a time.
    //    Spring Data handles COUNT query automatically.
    // ----------------------------------------------------------------
    @Query(value = """
            SELECT s FROM Sales s
            WHERE (:dateFrom  IS NULL OR s.sellDate       >= :dateFrom)
              AND (:dateTo    IS NULL OR s.sellDate       <= :dateTo)
              AND (:status    IS NULL OR s.status          = :status)
              AND (:customer  IS NULL OR LOWER(s.customerName)
                                        LIKE LOWER(CONCAT('%', :customer, '%')))
            """,
            countQuery = """
            SELECT COUNT(s) FROM Sales s
            WHERE (:dateFrom  IS NULL OR s.sellDate       >= :dateFrom)
              AND (:dateTo    IS NULL OR s.sellDate       <= :dateTo)
              AND (:status    IS NULL OR s.status          = :status)
              AND (:customer  IS NULL OR LOWER(s.customerName)
                                        LIKE LOWER(CONCAT('%', :customer, '%')))
            """)
    Page<Sales> fetchSalesPage(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo")   LocalDate dateTo,
            @Param("status") OrderStatus status,
            @Param("customer") String customer,
            Pageable pageable
    );

    // ----------------------------------------------------------------
    // 3. Fetch items for a specific page of sale IDs
    //    Avoids the HibernateJpaDialect "cannot simultaneously fetch
    //    multiple bags" problem on paginated queries.
    // ----------------------------------------------------------------
    @Query("""
            SELECT s FROM Sales s
            LEFT JOIN FETCH s.items
            WHERE s.id IN :ids
            """)
    List<Sales> fetchItemsForIds(@Param("ids") List<Long> ids);

    // ----------------------------------------------------------------
    // 4. Aggregate paid amounts per sale in ONE query
    //    Returns Object[]{salesId (Long), paidTotal (BigDecimal)}
    //    Called with the list of sale IDs already loaded — avoids
    //    an N+1 loop over payments.
    // ----------------------------------------------------------------
    @Query("""
            SELECT i.sales.id, SUM(i.amount)
            FROM Income i
            WHERE i.sales.id IN :ids
            GROUP BY i.sales.id
            """)
    List<Object[]> sumPaymentsByIds(@Param("ids") List<Long> ids);

    // ----------------------------------------------------------------
    // 5. Lightweight summary stats (no entity hydration at all)
    //    Used by the summary section of the report — avoids loading
    //    item/payment collections just to count and sum.
    // ----------------------------------------------------------------
    @Query("""
            SELECT
                COUNT(s),
                COALESCE(SUM(s.discount), 0),
                COALESCE(SUM(s.vat),      0)
            FROM Sales s
            WHERE (:dateFrom  IS NULL OR s.sellDate       >= :dateFrom)
              AND (:dateTo    IS NULL OR s.sellDate       <= :dateTo)
              AND (:status    IS NULL OR s.status          = :status)
              AND (:customer  IS NULL OR LOWER(s.customerName)
                                        LIKE LOWER(CONCAT('%', :customer, '%')))
            """)
    Object[] fetchSummaryStats(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo")   LocalDate dateTo,
            @Param("status")   OrderStatus status,
            @Param("customer") String customer
    );
}
