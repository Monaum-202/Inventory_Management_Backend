package com.monaum.Rapid_Global.module.personnel.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Monaum Hossain
 * @since Oct 21, 2025
 */

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Long> {

    List<Employee> findAllByActive(boolean active);

    @Query("""
        SELECT e FROM Employee e
        WHERE 
            (e.employeeId) LIKE CONCAT('%', :search, '%')
            OR LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%'))
            OR e.phone LIKE CONCAT('%', :search, '%')
        """)
    List<Employee> search(@Param("search") String search);

    @Query("SELECT e FROM Employee e ORDER BY e.id DESC LIMIT 1")
    Optional<Employee> findLastEmployee();

}
