package com.monaum.Rapid_Global.module.incomes.customer;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {

    Optional<Customer> findByPhone(String phone);

    @Query("""
        SELECT c FROM Customer c
        WHERE 
            LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(c.companyName) LIKE LOWER(CONCAT('%', :search, '%'))
            OR c.phone LIKE CONCAT('%', :search, '%')
        """)
    Page<Customer> search(@Param("search") String search,  Pageable pageable);

    //dashboard
    @Query("SELECT COUNT(c) FROM Customer c " +
            "WHERE c.createdAt BETWEEN :startDate AND :endDate " )
    Optional<BigDecimal> sumCustomerByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
