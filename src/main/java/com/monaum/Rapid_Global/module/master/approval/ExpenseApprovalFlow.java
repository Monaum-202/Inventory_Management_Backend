package com.monaum.Rapid_Global.module.master.approval;

import com.monaum.Rapid_Global.enums.ApprovalStatus;
import com.monaum.Rapid_Global.model.AbstractModel;
import com.monaum.Rapid_Global.module.expenses.expense.Expense;
import com.monaum.Rapid_Global.module.personnel.user.User;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

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
@Table(name = "EXPENSE_APPROVAL_FLOW")
public class ExpenseApprovalFlow extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_level_id", nullable = false)
    private ApprovalLevel approvalLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private User approver;

    @Column(nullable = false)
    private Integer sequenceOrder; // Order in which approvals should happen

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING;

    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;

    @Column(length = 1000)
    private String comments;

    @Column(length = 1000)
    private String rejectionReason;

    @Column(nullable = false)
    private Boolean isCurrentLevel = false; // Indicates if this is the current pending approval
}