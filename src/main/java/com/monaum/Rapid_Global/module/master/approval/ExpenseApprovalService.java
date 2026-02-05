//package com.monaum.Rapid_Global.module.master.approval;
//
//
//import com.monaum.Rapid_Global.enums.ApprovalStatus;
//import com.monaum.Rapid_Global.enums.ExpenseStatus;
//import com.monaum.Rapid_Global.module.expenses.expense.Expense;
//import com.monaum.Rapid_Global.module.expenses.expense.ExpenseRepo;
//import com.monaum.Rapid_Global.module.personnel.user.User;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class ExpenseApprovalService {
//
//    private final ExpenseRepo expenseRepository;
//    private final ApprovalLevelRepository approvalLevelRepository;
//    private final UserApprovalAuthorityRepository userApprovalAuthorityRepository;
//
//    /**
//     * Initialize approval workflow when expense is submitted
//     */
//    @Transactional
//    public void initializeApprovalWorkflow(Expense expense) {
//        BigDecimal amount = expense.getAmount();
//
//        // Get all active approval levels ordered by sequence
//        List<ApprovalLevel> allLevels = approvalLevelRepository
//            .findByIsActiveTrueOrderByLevelOrderAsc();
//
//        // Determine which levels are required based on amount
//        List<ApprovalLevel> requiredLevels = allLevels.stream()
//            .filter(level -> level.getCanApproveUnlimited() ||
//                           amount.compareTo(level.getMaxApprovalAmount()) <= 0)
//            .limit(1) // Get the appropriate level
//            .toList();
//
//        // If amount exceeds all non-unlimited levels, need all levels
//        if (requiredLevels.isEmpty() ||
//            (!requiredLevels.get(0).getCanApproveUnlimited() &&
//             amount.compareTo(requiredLevels.get(0).getMaxApprovalAmount()) > 0)) {
//            requiredLevels = allLevels;
//        }
//
//        // Create approval flow entries
//        int sequence = 1;
//        for (ApprovalLevel level : requiredLevels) {
//            ExpenseApprovalFlow flow = new ExpenseApprovalFlow();
//            flow.setExpense(expense);
//            flow.setApprovalLevel(level);
//            flow.setSequenceOrder(sequence);
//            flow.setStatus(sequence == 1 ? ApprovalStatus.PENDING : ApprovalStatus.PENDING);
//            flow.setIsCurrentLevel(sequence == 1);
//
//            expense.getApprovalFlows().add(flow);
//            sequence++;
//        }
//
//        expense.setRequiredApprovalLevels(requiredLevels.size());
//        expense.setCurrentApprovalLevel(1);
//        expense.setStatus(ExpenseStatus.SUBMITTED);
//        expense.setSubmittedAt(LocalDateTime.now());
//
//        expenseRepository.save(expense);
//    }
//
//    /**
//     * Process approval at current level
//     */
//    @Transactional
//    public void approveExpense(Long expenseId, User approver, String comments) {
//        Expense expense = expenseRepository.findById(expenseId)
//            .orElseThrow(() -> new RuntimeException("Expense not found"));
//
//        // Validate approver has authority
//        validateApproverAuthority(expense, approver);
//
//        // Get current approval flow
//        ExpenseApprovalFlow currentFlow = expense.getApprovalFlows().stream()
//            .filter(ExpenseApprovalFlow::getIsCurrentLevel)
//            .findFirst()
//            .orElseThrow(() -> new RuntimeException("No pending approval found"));
//
//        // Update current flow
//        currentFlow.setApprover(approver);
//        currentFlow.setStatus(ApprovalStatus.APPROVED);
//        currentFlow.setApprovedAt(LocalDateTime.now());
//        currentFlow.setComments(comments);
//        currentFlow.setIsCurrentLevel(false);
//
//        // Check if more approvals needed
//        if (expense.getCurrentApprovalLevel() < expense.getRequiredApprovalLevels()) {
//            // Move to next level
//            expense.setCurrentApprovalLevel(expense.getCurrentApprovalLevel() + 1);
//
//            ExpenseApprovalFlow nextFlow = expense.getApprovalFlows().stream()
//                .filter(f -> f.getSequenceOrder().equals(expense.getCurrentApprovalLevel()))
//                .findFirst()
//                .orElseThrow();
//
//            nextFlow.setIsCurrentLevel(true);
//            expense.setStatus(getStatusForLevel(expense.getCurrentApprovalLevel()));
//        } else {
//            // Final approval
//            expense.setStatus(ExpenseStatus.APPROVED);
//            expense.setFinalApprovedAt(LocalDateTime.now());
//            expense.setFinalApprovedBy(approver);
//        }
//
//        expenseRepository.save(expense);
//    }
//
//    /**
//     * Reject expense
//     */
//    @Transactional
//    public void rejectExpense(Long expenseId, User approver, String reason) {
//        Expense expense = expenseRepository.findById(expenseId)
//            .orElseThrow(() -> new RuntimeException("Expense not found"));
//
//        validateApproverAuthority(expense, approver);
//
//        ExpenseApprovalFlow currentFlow = expense.getApprovalFlows().stream()
//            .filter(ExpenseApprovalFlow::getIsCurrentLevel)
//            .findFirst()
//            .orElseThrow(() -> new RuntimeException("No pending approval found"));
//
//        currentFlow.setApprover(approver);
//        currentFlow.setStatus(ApprovalStatus.REJECTED);
//        currentFlow.setRejectedAt(LocalDateTime.now());
//        currentFlow.setRejectionReason(reason);
//        currentFlow.setIsCurrentLevel(false);
//
//        expense.setStatus(ExpenseStatus.REJECTED);
//        expense.setCancelReason(reason);
//
//        expenseRepository.save(expense);
//    }
//
//    private void validateApproverAuthority(Expense expense, User approver) {
//        ExpenseApprovalFlow currentFlow = expense.getApprovalFlows().stream()
//            .filter(ExpenseApprovalFlow::getIsCurrentLevel)
//            .findFirst()
//            .orElseThrow(() -> new RuntimeException("No pending approval"));
//
//        // Check if user has authority for this level
//        boolean hasAuthority = userApprovalAuthorityRepository
//            .existsByUserAndApprovalLevelAndIsActiveTrue(
//                approver,
//                currentFlow.getApprovalLevel()
//            );
//
//        if (!hasAuthority) {
//            throw new RuntimeException("User does not have authority to approve at this level");
//        }
//
//        // Additional check for amount limit
//        ApprovalLevel level = currentFlow.getApprovalLevel();
//        if (!level.getCanApproveUnlimited() &&
//            expense.getAmount().compareTo(level.getMaxApprovalAmount()) > 0) {
//            throw new RuntimeException("Amount exceeds approver's limit");
//        }
//    }
//
//    private ExpenseStatus getStatusForLevel(Integer level) {
//        return switch (level) {
//            case 1 -> ExpenseStatus.PENDING_LEVEL_1;
//            case 2 -> ExpenseStatus.PENDING_LEVEL_2;
//            case 3 -> ExpenseStatus.PENDING_LEVEL_3;
//            default -> ExpenseStatus.SUBMITTED;
//        };
//    }
//}