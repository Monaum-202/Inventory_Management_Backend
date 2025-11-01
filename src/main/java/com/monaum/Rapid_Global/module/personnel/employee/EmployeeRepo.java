package com.monaum.Rapid_Global.module.personnel.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Monaum Hossain
 * @since Oct 21, 2025
 */

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Long> {

    Page<Employee> findAllByStatus(boolean status, Pageable pageable);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByPhoneAndIdNot(String phone, Long id);

}
