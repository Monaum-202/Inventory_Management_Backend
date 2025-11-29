package com.monaum.Rapid_Global.module.master.transectionCategory;

import com.monaum.Rapid_Global.enums.TransactionType;
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
 *
 * @since 14-Nov-25 10:25 AM
 */

@Repository
public interface TransactionCategoryRepo extends JpaRepository<TransactionCategory,Long> {

    List<TransactionCategory> findAllByActiveAndType(Boolean active, TransactionType type);

    @Query("""
        SELECT tc FROM TransactionCategory tc
        WHERE LOWER(tc.name) LIKE LOWER(CONCAT('%', :search, '%'))
        """)
    List<TransactionCategory> search(@Param("search") String search);
}
