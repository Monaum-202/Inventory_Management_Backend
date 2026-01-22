package com.monaum.Rapid_Global.module.master.permission;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 22-Jan-26 11:32 PM
 */

public interface RoleMenuRepository extends JpaRepository<RoleMenu, Long> {
    void deleteByRoleId(Long roleId);
}
