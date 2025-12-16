package com.monaum.Rapid_Global.module.expenses.purchase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monaum.Rapid_Global.enums.OrderStatus;
import com.monaum.Rapid_Global.model.AbstractModel;
import com.monaum.Rapid_Global.module.expenses.expense.Expense;
import com.monaum.Rapid_Global.module.expenses.purchaseItem.PurchaseItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 01-Dec-25 10:52 PM
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "purchase")
@EqualsAndHashCode(callSuper = false)
public class Purchase extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_no", unique = true, nullable = false)
    private String invoiceNo;

    @Column(nullable = false, length = 100)
    private String supplierName;

    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(length = 200)
    private String address;

    @Column(length = 200)
    private String companyName;

    @Column(nullable = false)
    private LocalDate purchaseDate;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "vat")
    private BigDecimal vat;

    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @JsonIgnore
    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseItem> items = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Expense> payments = new ArrayList<>();

    @Column(name = "cancel_reason")
    private String cancelReason;

}
