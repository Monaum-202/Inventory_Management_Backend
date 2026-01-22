package com.monaum.Rapid_Global.module.master.module;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 21-Jan-26 10:00 PM
 */

@Repository
public interface ModuleRepo extends JpaRepository<Module, Long> {

    @Query("""
        SELECT m FROM Module m
        WHERE m.id IN (
            SELECT rm.moduleId FROM RoleModule rm
            WHERE rm.roleId = :roleId
        )
        ORDER BY m.sqnce
    """)
    List<Module> findByRoleId(Long roleId);
}
