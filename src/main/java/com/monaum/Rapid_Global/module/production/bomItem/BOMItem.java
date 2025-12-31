package com.monaum.Rapid_Global.module.production.bomItem;

import com.monaum.Rapid_Global.module.master.product.Product;
import com.monaum.Rapid_Global.module.production.bom.BillOfMaterials;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bom_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BOMItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bom_id", nullable = false)
    private BillOfMaterials bom;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_id", nullable = false)
    private Product rawMaterial; // Must be a RAW_MATERIAL type product
    
    @Column(nullable = false)
    private Double quantity; // Required quantity
    
    @Column(length = 50)
    private String unit; // e.g., "kg", "gram", "liter", "ml"
    
    @Column(nullable = false)
    private Double unitCost = 0.0; // Cost per unit at time of creation
    
    @Column(nullable = false)
    private Double totalCost = 0.0; // quantity * unitCost
    
    @Column(length = 500)
    private String notes; // Special instructions for this ingredient
    
    @Column(nullable = false)
    private Boolean isOptional = false; // Is this ingredient optional?
    
    @Column(nullable = false)
    private Integer sequenceOrder = 0; // Order in which to add ingredients
    
    @Column(nullable = false)
    private Boolean isActive = true;
}