package com.monaum.Rapid_Global.module.production.bom;

import com.monaum.Rapid_Global.enums.BOMStatus;
import com.monaum.Rapid_Global.model.AbstractModel;
import com.monaum.Rapid_Global.module.master.product.Product;
import com.monaum.Rapid_Global.module.personnel.user.User;
import com.monaum.Rapid_Global.module.production.bomItem.BOMItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bill_of_materials")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillOfMaterials extends AbstractModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String bomCode; // e.g., BOM-001
    
    @Column(nullable = false, length = 200)
    private String bomName; // e.g., "Chocolate Cake Recipe"
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finished_product_id", nullable = false)
    private Product finishedProduct;
    
    @Column(nullable = false)
    private Double outputQuantity = 1.0; // How many units this BOM produces
    
    @Column(length = 50)
    private String outputUnit; // e.g., "pcs", "kg", "liter"
    
    @OneToMany(mappedBy = "bom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BOMItem> items = new ArrayList<>();
    
    @Column(length = 500)
    private String description;
    
    @Column(length = 1000)
    private String productionNotes; // Instructions for production
    
    @Column(nullable = false)
    private Double estimatedCost = 0.0; // Total cost of raw materials
    
    @Column(nullable = false)
    private Double laborCost = 0.0; // Labor cost per batch
    
    @Column(nullable = false)
    private Double overheadCost = 0.0; // Overhead/utility costs
    
    @Column(nullable = false)
    private Double totalCost = 0.0; // estimatedCost + laborCost + overheadCost
    
    @Column(nullable = false)
    private Integer estimatedTimeMinutes = 0; // Production time
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BOMStatus status = BOMStatus.DRAFT;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(nullable = false)
    private Boolean isDefault = false; // Is this the default BOM for this product?
    
    @Column(length = 20)
    private String version = "1.0"; // Version control for BOM

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;
    
    private LocalDateTime approvedDate;
}