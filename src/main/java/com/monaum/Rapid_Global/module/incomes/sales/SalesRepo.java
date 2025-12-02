package com.monaum.Rapid_Global.module.incomes.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesRepo extends JpaRepository<Sales, Long> {

    @Query(value = "SELECT invoice_no FROM SALES " +
            "WHERE invoice_no LIKE CONCAT('INv', SUBSTRING(YEAR(CURDATE()),3,2), '%') " +
            "ORDER BY CAST(SUBSTRING(invoice_no, 7) AS UNSIGNED) DESC " +
            "LIMIT 1 FOR UPDATE",
            nativeQuery = true)
    String findLastInvoiceNoForUpdate();
}
