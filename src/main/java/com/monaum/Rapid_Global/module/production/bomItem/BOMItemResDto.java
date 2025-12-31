package com.monaum.Rapid_Global.module.production.bomItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BOMItemResDto {
    private Long id;
    private Long rawMaterialId;
    private String rawMaterialName;
    private String rawMaterialCode;
    private Double quantity;
    private String unit;
    private Double unitCost;
    private Double totalCost;
    private String notes;
    private Boolean isOptional;
    private Integer sequenceOrder;
    private Double availableStock; // Current stock of this raw material
}