package com.monaum.Rapid_Global.module.production.bom;

import com.monaum.Rapid_Global.enums.BOMStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BOMResDto {
    
    private Long id;
    private String bomCode;
    private String bomName;
    private Long finishedProductId;
    private String finishedProductName;
    private Double outputQuantity;
    private String outputUnit;
    private String description;
    private String productionNotes;
    private Double estimatedCost;
    private Double laborCost;
    private Double overheadCost;
    private Double totalCost;
    private Integer estimatedTimeMinutes;
    private BOMStatus status;
    private Boolean isActive;
    private Boolean isDefault;
    private String version;
    private List<BOMItemResDto> items;
    private String createdByName;
    private LocalDateTime createdDate;
    private String updatedByName;
    private LocalDateTime updatedDate;
    private String approvedByName;
    private LocalDateTime approvedDate;
}