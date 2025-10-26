package com.monaum.Rapid_Global.module.master.product_log;

import com.monaum.Rapid_Global.module.master.product.Product;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Long changedBy;

    @Column(length = 50)
    private String oldName;

    @Column(length = 50)
    private String newName;

    @Column(length = 255)
    private String oldDescription;

    @Column(length = 255)
    private String newDescription;

    private Long oldUnitId;
    private Long newUnitId;

    @Column(precision = 10, scale = 2)
    private BigDecimal oldPricePerUnit;

    @Column(precision = 10, scale = 2)
    private BigDecimal newPricePerUnit;

    private Integer oldStatus;
    private Integer newStatus;

    private Integer changeType;

    private LocalDateTime changedAt;

    private Long companyId;
}

