package com.monaum.Rapid_Global.module.incomes.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesRepo extends JpaRepository<Sales, Long> {
}
