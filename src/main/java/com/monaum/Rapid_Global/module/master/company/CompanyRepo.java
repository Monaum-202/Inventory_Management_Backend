package com.monaum.Rapid_Global.module.master.company;

import com.monaum.Rapid_Global.module.personnel.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Repository
public interface CompanyRepo extends JpaRepository<Company, Long> {

    Page<Company> findAllByCreatedBy(User createdBy, Pageable pageable);
}
