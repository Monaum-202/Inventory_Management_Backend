package com.monaum.Rapid_Global.module.incomes.estimate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monaum.Rapid_Global.enums.OrderStatus;
import com.monaum.Rapid_Global.model.AbstractModel;
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
 * @since 12-Dec-25
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Estimate")
@EqualsAndHashCode(callSuper = false)
public class Estimate extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "estimate_no", unique = true, nullable = false)
    private String estimateNo;

    @Column(length = 100)
    private String customerName;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(length = 200)
    private String address;

    @Column(length = 200)
    private String companyName;

    @Column(nullable = false)
    private LocalDate estimateDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "vat")
    private Double vat;

    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @JsonIgnore
    @OneToMany(mappedBy = "estimate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EstimateItem> items = new ArrayList<>();

    @Column(name = "converted_to_sale")
    private Boolean convertedToSale = false;

    @Column(name = "sale_id")
    private Long saleId;
}