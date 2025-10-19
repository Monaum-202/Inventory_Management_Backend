package com.monaum.Rapid_Global.module.company;

import com.monaum.Rapid_Global.module.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

//@Repository
public interface CompanyRepo extends JpaRepository<Company, Long> {

    Page<Company> findAllByCreatedBy(User createdBy, Pageable pageable);
}
