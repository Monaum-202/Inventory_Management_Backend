package com.monaum.Rapid_Global.module.production.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monaum.Rapid_Global.module.production.itemUsage.ItemUsage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 25-Dec-25 1:43 AM
 */

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(nullable = false)
    private String itemName;

    @Column(name = "unit_name")
    private  String unitName;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "note")
    private String note;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_usage_id", nullable = false)
    private ItemUsage itemUsage;
}
