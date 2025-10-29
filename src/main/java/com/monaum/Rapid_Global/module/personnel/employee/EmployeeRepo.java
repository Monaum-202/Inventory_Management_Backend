package com.monaum.Rapid_Global.module.personnel.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Long> {

    Page<Employee> findAllByStatus(boolean status, Pageable pageable);
}
