package com.monaum.Rapid_Global.module.master.product;
import com.monaum.Rapid_Global.enums.ProductType;
import com.monaum.Rapid_Global.model.AbstractModel;
import com.monaum.Rapid_Global.module.master.company.Company;
import com.monaum.Rapid_Global.module.master.unit.Unit;
import jakarta.persistence.*;
        import lombok.*;

        import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    @Column(length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Column(name = "sorting_order")
    private Integer sortingOrder;

    @Column(name = "price_per_unit", precision = 10, scale = 2)
    private BigDecimal pricePerUnit;

    @Column(name = "active")
    private Boolean active = true;

//    // --- Stock Tracking Fields ---
//    @Column(name = "stock_quantity", nullable = false)
//    private Integer stockQuantity = 0;
//
//    @Column(name = "reorder_level")
//    private Integer reorderLevel = 0;
//
//    @Column(name = "is_stock_tracked")
//    private Boolean isStockTracked = true;
}

