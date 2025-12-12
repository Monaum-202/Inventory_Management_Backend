package com.monaum.Rapid_Global.module.incomes.estimate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 12-Dec-25
 */

@Repository
public interface EstimateRepo extends JpaRepository<Estimate, Long> {

    @Query(value = "SELECT estimate_no FROM estimate ORDER BY id DESC LIMIT 1 FOR UPDATE", nativeQuery = true)
    String findLastEstimateNoForUpdate();
}