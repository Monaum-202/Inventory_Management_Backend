package com.monaum.Rapid_Global.module.master.product;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private String name;
    private String productType;
    private String description;
    private Long unitId;
    private String unitName;
    private Integer sortingOrder;
    private BigDecimal pricePerUnit;
    private Long companyId;
    private String companyName;
    private Boolean status;
}

