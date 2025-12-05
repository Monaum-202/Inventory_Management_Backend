package com.monaum.Rapid_Global.module.expenses.supplier;

import com.monaum.Rapid_Global.model.AbstractModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Supplier")
@EqualsAndHashCode(callSuper = false)
public class Supplier extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "phone", nullable = false,  length = 50)
    private String phone;


    @Column(name = "alt_phone", length = 20)
    private Long altPhone;

    @Column(name = "email")
    private String email;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "business_address", length = 255)
    private String businessAddress;

    @Column(name = "total_transanction", precision = 10, scale = 2)
    private BigDecimal totalTransaction;

    @Column(name = "due", precision = 10, scale = 2)
    private BigDecimal due;

    @Column(name = "company_id")
    private Long companyId;

}
