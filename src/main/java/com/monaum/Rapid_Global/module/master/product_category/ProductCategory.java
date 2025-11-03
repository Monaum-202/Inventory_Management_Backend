package com.monaum.Rapid_Global.module.master.product_category;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Product_category")
@EqualsAndHashCode(callSuper = false)
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "code", length = 50, unique = true)
    private String code;

    @Column(name = "description", unique = true, columnDefinition = "TEXT", length = 50)
    private String description;

    @Column(name = "status", nullable = false, length = 50)
    private Boolean status = true;

    @Column(nullable = false)
    private Integer sqn;

}
