package com.monaum.Rapid_Global.module.master.approval;

import com.monaum.Rapid_Global.model.AbstractModel;
import com.monaum.Rapid_Global.module.personnel.user.User;
import lombok.*;
import jakarta.persistence.*;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 30-Jan-26 7:09 PM
 */


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USER_APPROVAL_AUTHORITY")
public class UserApprovalAuthority extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_level_id", nullable = false)
    private ApprovalLevel approvalLevel;

    @Column(nullable = false)
    private Boolean isActive = true;

    // Optional: Department or region specific authority
    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "region")
    private String region;
}