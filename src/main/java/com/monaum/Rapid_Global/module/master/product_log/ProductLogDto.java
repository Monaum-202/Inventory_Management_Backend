package com.monaum.Rapid_Global.module.master.product_log;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductLogDto {

    private Long id;
    private Long productId;
    private Long changedBy;

    private String oldName;
    private String newName;

    private String oldDescription;
    private String newDescription;

    private Long oldUnitId;
    private Long newUnitId;

    private BigDecimal oldPricePerUnit;
    private BigDecimal newPricePerUnit;

    private Integer oldStatus;
    private Integer newStatus;

    private Integer changeType;

    private LocalDateTime changedAt;

    private Long companyId;
}
