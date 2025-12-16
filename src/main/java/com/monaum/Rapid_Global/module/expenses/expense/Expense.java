package com.monaum.Rapid_Global.module.expenses.expense;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monaum.Rapid_Global.enums.Status;
import com.monaum.Rapid_Global.model.AbstractModel;
import com.monaum.Rapid_Global.module.expenses.expense_category.ExpenseCategory;
import com.monaum.Rapid_Global.module.expenses.purchase.Purchase;
import com.monaum.Rapid_Global.module.incomes.sales.Sales;
import com.monaum.Rapid_Global.module.master.paymentMethod.PaymentMethod;
import com.monaum.Rapid_Global.module.master.transectionCategory.TransactionCategory;
import com.monaum.Rapid_Global.module.personnel.employee.Employee;
import com.monaum.Rapid_Global.module.personnel.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Monaum Hossain
 * @since oct 21, 2025
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "EXPENSE")
@EqualsAndHashCode(callSuper = false)
public class Expense extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name ="expense_id", unique = true)
    private String expenseId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",  nullable = false)
    private TransactionCategory expenseCategory;

    private BigDecimal amount;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    @Column(name = "transection_id")
    private String transactionId;

    @Column(name = "paid_to")
    private String paidTo;

    @Column(name = "paid_to_company")
    private String paidToCompany;

    private LocalDate expenseDate;

    @Column(name = "description")
    private String description;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    private LocalDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    @JsonIgnore
    private Employee employee;

    @Column(name = "status")
    private Status status = Status.PENDING;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "created_by_name")
    private String createdByName;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;
}
