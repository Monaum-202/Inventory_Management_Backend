package com.monaum.Rapid_Global.module.production.bomItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BOMItemReqDto {
    private Long rawMaterialId;
    private Double quantity;
    private String unit;
    private String notes;
    private Boolean isOptional;
    private Integer sequenceOrder;
}