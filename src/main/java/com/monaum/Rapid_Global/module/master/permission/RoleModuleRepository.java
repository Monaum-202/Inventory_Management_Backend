package com.monaum.Rapid_Global.module.master.permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 22-Jan-26 11:31 PM
 */

public interface RoleModuleRepository extends JpaRepository<RoleModule, Long> {
    @Modifying
    @Query("DELETE FROM RoleModule rm WHERE rm.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") Long roleId);
}
