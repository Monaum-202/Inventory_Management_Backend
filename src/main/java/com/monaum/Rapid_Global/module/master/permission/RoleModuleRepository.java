package com.monaum.Rapid_Global.module.master.permission;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 22-Jan-26 11:31 PM
 */

public interface RoleModuleRepository extends JpaRepository<RoleModule, Long> {
    void deleteByRoleId(Long roleId);
}
