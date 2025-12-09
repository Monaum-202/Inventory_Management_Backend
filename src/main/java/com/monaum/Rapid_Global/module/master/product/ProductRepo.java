package com.monaum.Rapid_Global.module.master.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 09-Dec-25 8:57 PM
 */

@Repository
public interface ProductRepo extends JpaRepository<Product,Long> {

    List<Product> findAllByActive(Boolean status);

    @Query("""
    SELECT u FROM Product u
    WHERE 
        LOWER(COALESCE(u.name, '')) LIKE LOWER(CONCAT('%', :search, '%'))
""")
    List<Product> search(@Param("search") String search);


        @Query("""
        SELECT p FROM Product p
        WHERE (:search IS NULL OR LOWER(COALESCE(p.name, '')) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:status IS NULL OR p.active = :status)
    """)
        List<Product> searchAndFilter(@Param("search") String search, @Param("status") Boolean status);
    }
