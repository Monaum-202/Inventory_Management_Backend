package com.monaum.Rapid_Global.module.expenses.purchase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
