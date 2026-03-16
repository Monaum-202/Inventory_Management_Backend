package com.monaum.Rapid_Global.module.expenses.purchase;

import com.monaum.Rapid_Global.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 17-Dec-25 12:12 AM
 */

@Repository
public interface PurchaseRepo extends JpaRepository<Purchase, Long> {

    @Query("""
    SELECT p FROM Purchase p
    WHERE 
        LOWER(COALESCE(p.invoiceNo, '')) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(COALESCE(p.phone, '')) LIKE LOWER(CONCAT('%', :search, '%'))
""")
    Page<Purchase> search(@Param("search") String search, Pageable pageable);

    @Query(value = """
    SELECT invoice_no
    FROM purchase
    WHERE invoice_no LIKE CONCAT('PUR', DATE_FORMAT(CURDATE(), '%y%m'), '%')
    ORDER BY CAST(SUBSTRING(invoice_no, 8) AS UNSIGNED) DESC
    LIMIT 1
    FOR UPDATE
    """,
            nativeQuery = true)
    String findLastInvoiceNoForUpdate();

    @Query("""
    SELECT 
      (SUM(i.totalPrice) 
       - COALESCE(s.discount, 0)
       + (SUM(i.totalPrice) * (COALESCE(s.vat, 0) / 100))
      )
    FROM Purchase s
    JOIN s.items i
    WHERE s.supplierId = :supplierId
    GROUP BY s.id
""")
    List<BigDecimal> calculatePerSaleTotalsByCustomer(Long supplierId);

    @Query("""
                SELECT 
                  COALESCE(SUM(pi.totalPrice), 0)
                  -
                  COALESCE(SUM(e.amount), 0)
                FROM Purchase p
                JOIN p.items pi
                LEFT JOIN p.payments e
                       ON e.status = com.monaum.Rapid_Global.enums.Status.APPROVED
                       AND e.expenseDate BETWEEN :fromDate AND :toDate
                WHERE p.purchaseDate BETWEEN :fromDate AND :toDate
            """)
    BigDecimal getDueAmountByDateRange(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("""
            SELECT p FROM Purchase p
            LEFT JOIN FETCH p.items
            WHERE (:dateFrom  IS NULL OR p.purchaseDate       >= :dateFrom)
              AND (:dateTo    IS NULL OR p.purchaseDate       <= :dateTo)
              AND (:status    IS NULL OR p.status              = :status)
              AND (:supplier  IS NULL OR LOWER(p.supplierName)
                                        LIKE LOWER(CONCAT('%', :supplier, '%')))
            ORDER BY p.purchaseDate DESC, p.id DESC
            """)
    List<Purchase> fetchPurchasesWithItems(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo")   LocalDate dateTo,
            @Param("status")   OrderStatus status,
            @Param("supplier") String supplier
    );

    // ----------------------------------------------------------------
    // 2. Paginated query — no fetch join (avoids HHH-90003004)
    //    Used by: JSON endpoint so the caller gets one page at a time.
    // ----------------------------------------------------------------
    @Query(value = """
            SELECT p FROM Purchase p
            WHERE (:dateFrom  IS NULL OR p.purchaseDate       >= :dateFrom)
              AND (:dateTo    IS NULL OR p.purchaseDate       <= :dateTo)
              AND (:status    IS NULL OR p.status              = :status)
              AND (:supplier  IS NULL OR LOWER(p.supplierName)
                                        LIKE LOWER(CONCAT('%', :supplier, '%')))
            """,
            countQuery = """
            SELECT COUNT(p) FROM Purchase p
            WHERE (:dateFrom  IS NULL OR p.purchaseDate       >= :dateFrom)
              AND (:dateTo    IS NULL OR p.purchaseDate       <= :dateTo)
              AND (:status    IS NULL OR p.status              = :status)
              AND (:supplier  IS NULL OR LOWER(p.supplierName)
                                        LIKE LOWER(CONCAT('%', :supplier, '%')))
            """)
    Page<Purchase> fetchPurchasesPage(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo")   LocalDate dateTo,
            @Param("status") OrderStatus status,
            @Param("supplier") String supplier,
            Pageable pageable
    );

    // ----------------------------------------------------------------
    // 3. Fetch items for a specific page of purchase IDs
    //    Avoids the "cannot simultaneously fetch multiple bags" problem.
    // ----------------------------------------------------------------
    @Query("""
            SELECT p FROM Purchase p
            LEFT JOIN FETCH p.items
            WHERE p.id IN :ids
            """)
    List<Purchase> fetchItemsForIds(@Param("ids") List<Long> ids);

    // ----------------------------------------------------------------
    // 4. Aggregate paid amounts per purchase in ONE query
    //    Returns Object[]{purchaseId (Long), paidTotal (BigDecimal)}
    //    Uses the Expense entity (purchase-side payment), identical
    //    pattern to the Income query on the sales side.
    // ----------------------------------------------------------------
    @Query("""
            SELECT e.purchase.id, SUM(e.amount)
            FROM Expense e
            WHERE e.purchase.id IN :ids
            GROUP BY e.purchase.id
            """)
    List<Object[]> sumPaymentsByIds(@Param("ids") List<Long> ids);
}
