package com.monaum.Rapid_Global.module.master.paymentMethod;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 29-Oct-25 9:49 PM
 */

@Repository
public interface RepoPaymentMethod extends JpaRepository<PaymentMethod, Long> {

    List<PaymentMethod> findAllByActive(Boolean status);

    @Query("""
        SELECT p FROM PaymentMethod p
        WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
        """)
    List<PaymentMethod> search(@Param("search") String search);
}
