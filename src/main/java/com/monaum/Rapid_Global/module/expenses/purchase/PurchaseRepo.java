package com.monaum.Rapid_Global.module.expenses.purchase;

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

}
