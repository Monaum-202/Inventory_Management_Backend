package com.monaum.Rapid_Global.module.production.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {
    private Long productId;

    private String itemName;

    private String unitName;

    private BigDecimal quantity;

    private String note;
}
