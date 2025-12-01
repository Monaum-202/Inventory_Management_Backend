package com.monaum.Rapid_Global.module.incomes.customer;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {

    Optional<Customer> findByPhone(String phone);

    @Query("""
        SELECT c FROM Customer c
        WHERE 
            LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(c.businessName) LIKE LOWER(CONCAT('%', :search, '%'))
            OR c.phone LIKE CONCAT('%', :search, '%')
        """)
    Page<Customer> search(@Param("search") String search,  Pageable pageable);
}
