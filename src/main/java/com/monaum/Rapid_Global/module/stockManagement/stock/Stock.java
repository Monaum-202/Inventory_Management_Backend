package com.monaum.Rapid_Global.module.stockManagement.stock;

import com.monaum.Rapid_Global.model.AbstractModel;
import com.monaum.Rapid_Global.module.master.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 17-Dec-25 11:39 AM
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stock")
@EqualsAndHashCode(callSuper = false)
public class Stock extends AbstractModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;
    
    @Column(name = "quantity", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantity = BigDecimal.ZERO;
    
    @Column(name = "alert_quantity", precision = 15, scale = 3)
    private BigDecimal alertQuantity;
    
    @Column(name = "average_cost", precision = 15, scale = 2)
    private BigDecimal averageCost = BigDecimal.ZERO;
    
    @Version
    private Long version;
}