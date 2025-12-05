package com.monaum.Rapid_Global.module.incomes.customer;

import com.monaum.Rapid_Global.model.AbstractModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Customer")
@EqualsAndHashCode(callSuper = false)
public class Customer extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = false , length = 50)
    private String name;

    @Column(name = "PHONE", nullable = false, length = 20)
    private String phone;

    @Column(name = "alt_phone", length = 20)
    private String altPhone;

    @Column(name = "Email", nullable = false, length = 20)
    private String email;

    @Column(name = "Address", length = 100)
    private String address;

    @Column(name = "company_name", length = 100)
    private String companyName;

    @Column(name = "total_transaction", precision = 10, scale = 2)
    private BigDecimal totalTransaction;
}
