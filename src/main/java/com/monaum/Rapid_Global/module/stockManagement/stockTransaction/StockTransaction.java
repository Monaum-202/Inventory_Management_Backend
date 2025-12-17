package com.monaum.Rapid_Global.module.stockManagement.stockTransaction;

import com.monaum.Rapid_Global.enums.StockType;
import com.monaum.Rapid_Global.model.AbstractModel;
import com.monaum.Rapid_Global.module.master.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 17-Dec-25 11:39 AM
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stock_transaction")
@EqualsAndHashCode(callSuper = false)
public class StockTransaction extends AbstractModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private StockType type;
    
    @Column(name = "quantity", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantity; // Positive = IN, Negative = OUT
    
    @Column(name = "unit_cost", precision = 15, scale = 2)
    private BigDecimal unitCost;
    
    @Column(name = "balance", nullable = false, precision = 15, scale = 3)
    private BigDecimal balance;
    
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @Column(name = "reference_type", length = 50)
    private String referenceType; // PURCHASE, SALE, ADJUSTMENT
    
    @Column(name = "reference_id")
    private Long referenceId;
    
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
}