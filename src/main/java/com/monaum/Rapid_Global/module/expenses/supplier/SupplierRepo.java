package com.monaum.Rapid_Global.module.expenses.supplier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepo extends JpaRepository <Supplier , Long > {

    Optional<Supplier> findByPhone(String phone);

    @Query("""
        SELECT s FROM Supplier s
        WHERE 
            LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(s.companyName) LIKE LOWER(CONCAT('%', :search, '%'))
            OR s.phone LIKE CONCAT('%', :search, '%')
        """)
    Page<Supplier> search(@Param("search") String search, Pageable pageable);

}
