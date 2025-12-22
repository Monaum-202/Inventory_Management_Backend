package com.monaum.Rapid_Global.module.master.product;

import com.monaum.Rapid_Global.enums.ProductType;
import jakarta.persistence.Column;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResDto {

    private Long id;
    private String name;
    private ProductType productType;
    private String description;
    private Long unitId;
    private String unitName;
    private Integer sortingOrder;
    private BigDecimal pricePerUnit;
    private Boolean active;
    private BigDecimal alertQuantity;
}

