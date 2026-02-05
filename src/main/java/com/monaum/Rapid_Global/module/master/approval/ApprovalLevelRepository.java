package com.monaum.Rapid_Global.module.master.approval;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * @since 31-Jan-26 12:11 AM
 */

@Repository
public interface ApprovalLevelRepository extends JpaRepository<ApprovalLevel, Long> {
    List<ApprovalLevel> findByIsActiveTrueOrderByLevelOrderAsc();
}
