package com.monaum.Rapid_Global.module.master.approval;

import com.monaum.Rapid_Global.module.personnel.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 31-Jan-26 12:12 AM
 */

@Repository
public interface UserApprovalAuthorityRepository extends JpaRepository<UserApprovalAuthority, Long> {
    boolean existsByUserAndApprovalLevelAndIsActiveTrue(User user, ApprovalLevel level);
    List<UserApprovalAuthority> findByUserAndIsActiveTrue(User user);
}