package com.monaum.Rapid_Global.module.master.unit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepo extends JpaRepository<Unit,Long> {

    @Query("""
    SELECT u FROM Unit u
    WHERE 
        LOWER(COALESCE(u.name, '')) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(COALESCE(u.fullName, '')) LIKE LOWER(CONCAT('%', :search, '%'))
""")
    List<Unit> search(@Param("search") String search);

}
