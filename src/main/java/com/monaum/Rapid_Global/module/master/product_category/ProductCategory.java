package com.monaum.Rapid_Global.module.master.product_category;

import com.monaum.Rapid_Global.model.AbstractModel;
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
public class ProductCategory extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "code", length = 50, unique = true, nullable = false)
    private String code;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @Column(name = "sqn",nullable = false)
    private Integer sqn;

}
