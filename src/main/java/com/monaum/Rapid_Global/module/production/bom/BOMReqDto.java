package com.monaum.Rapid_Global.module.production.bom;

import com.monaum.Rapid_Global.enums.BOMStatus;
import com.monaum.Rapid_Global.module.production.bomItem.BOMItemReqDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BOMReqDto {
    
    private String bomName;
    private Long finishedProductId;
    private Double outputQuantity;
    private String outputUnit;
    private String description;
    private String productionNotes;
    private Double laborCost;
    private Double overheadCost;
    private Integer estimatedTimeMinutes;
    private BOMStatus status;
    private Boolean isDefault;
    private String version;
    private List<BOMItemReqDto> items;
}