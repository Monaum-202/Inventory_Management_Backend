package com.monaum.Rapid_Global.module.incomes.sales;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monaum.Rapid_Global.enums.OrderStatus;
import com.monaum.Rapid_Global.enums.Status;
import com.monaum.Rapid_Global.model.AbstractModel;
import com.monaum.Rapid_Global.module.incomes.income.Income;
import com.monaum.Rapid_Global.module.incomes.salesItem.SalesItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
@Table(name = "Sales")
@EqualsAndHashCode(callSuper = false)
public class Sales extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_no", unique = true, nullable = false)
    private String invoiceNo;

    @Column(nullable = false, length = 100)
    private String customerName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(length = 200)
    private String address;

    @Column(length = 200)
    private String companyName;

    @Column(nullable = false)
    private LocalDate sellDate;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "vat")
    private Double vat;

    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @JsonIgnore
    @OneToMany(mappedBy = "sales", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalesItem> items = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "sales", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Income> payments = new ArrayList<>();

    @Column(name = "cancel_reason")
    private String cancelReason;

}
