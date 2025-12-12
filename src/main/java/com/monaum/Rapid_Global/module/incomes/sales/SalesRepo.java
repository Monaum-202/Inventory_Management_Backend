package com.monaum.Rapid_Global.module.incomes.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesRepo extends JpaRepository<Sales, Long> {

    @Query(value = "SELECT invoice_no FROM SALES " +
            "WHERE invoice_no LIKE CONCAT('INv', SUBSTRING(YEAR(CURDATE()),3,2), '%') " +
            "ORDER BY CAST(SUBSTRING(invoice_no, 7) AS UNSIGNED) DESC " +
            "LIMIT 1 FOR UPDATE",
            nativeQuery = true)
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
    List<Double> calculatePerSaleTotalsByCustomer(Long customerId);


}
