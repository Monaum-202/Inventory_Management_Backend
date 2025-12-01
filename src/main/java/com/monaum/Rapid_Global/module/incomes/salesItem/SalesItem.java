package com.monaum.Rapid_Global.module.incomes.salesItem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monaum.Rapid_Global.model.AbstractModel;
import com.monaum.Rapid_Global.module.incomes.sales.Sales;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 01-Dec-25 11:12 PM
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sales_item")
@EqualsAndHashCode(callSuper = false)
public class SalesItem extends AbstractModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double unitPrice;

    @Column(nullable = false)
    private Double totalPrice; // quantity * unitPrice

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_id", nullable = false)
    private Sales sales;
}
