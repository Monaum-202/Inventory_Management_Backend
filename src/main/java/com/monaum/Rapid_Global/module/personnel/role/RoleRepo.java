package com.monaum.Rapid_Global.module.personnel.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * @since 23-Nov-25 5:18 PM
 */
public interface RoleRepo extends JpaRepository<Role, Long> {

    @Query("SELECT r FROM Role r WHERE r.id <> 1")
    Page<Role> findAllExcept(Pageable pageable);

    boolean existsByName(String name);
}
