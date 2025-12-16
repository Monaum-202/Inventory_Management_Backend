package com.monaum.Rapid_Global.module.incomes.income;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monaum.Rapid_Global.enums.Status;
import com.monaum.Rapid_Global.model.AbstractModel;
import com.monaum.Rapid_Global.module.incomes.sales.Sales;
import com.monaum.Rapid_Global.module.master.paymentMethod.PaymentMethod;
import com.monaum.Rapid_Global.module.master.transectionCategory.TransactionCategory;
import com.monaum.Rapid_Global.module.personnel.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Income")
@EqualsAndHashCode(callSuper = false)
public class Income extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name ="income_id", unique = true)
    private String incomeId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private TransactionCategory incomeCategory;

    private BigDecimal amount;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_id")
    private Sales sales;

    @Column(name = "paid_from")
    private String paidFrom;

    @Column(name= "paid_from_id")
    private Long paidFromId;

    @Column(name = "paid_from_company")
    private String paidFromCompany;

    private LocalDate incomeDate;

    @Column(name = "description")
    private String description;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    private LocalDateTime approvedAt;

    @Column(name = "status")
    private Status status = Status.PENDING;

    @Column(name = "cancel_reason")
    private String cancelReason;
}
