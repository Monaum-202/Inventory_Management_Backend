package com.monaum.Rapid_Global.module.master.transectionCategory;

import com.monaum.Rapid_Global.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * @since 14-Nov-25 10:25 AM
 */

@Repository
public interface TransectionCategoryRepo extends JpaRepository<TransectionCategory,Long> {

    Page<TransectionCategory> findAllByActiveAndType(Boolean active, TransactionType type, Pageable pageable);

    @Query("""
        SELECT tc FROM TransectionCategory tc
        WHERE LOWER(tc.name) LIKE LOWER(CONCAT('%', :search, '%'))
        """)
    Page<TransectionCategory> search(@Param("search") String search, Pageable pageable);
}
