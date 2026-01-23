package com.monaum.Rapid_Global.module.master.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 21-Jan-26 10:01 PM
 */

@Repository
public interface MenuRepo extends JpaRepository<Menu, Long> {

    @Query("""
        SELECT m FROM Menu m
        WHERE m.moduleId = :moduleId
        AND m.id IN (
            SELECT rm.menuId FROM RoleMenu rm
            WHERE rm.roleId = :roleId
        )
        ORDER BY m.sqnce
    """)
    List<Menu> findByRoleAndModule(Long roleId, Long moduleId);

    List<Menu> findByModuleId(Long moduleId);
}
