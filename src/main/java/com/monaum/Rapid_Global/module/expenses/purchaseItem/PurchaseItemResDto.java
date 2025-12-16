package com.monaum.Rapid_Global.module.expenses.purchaseItem;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 16-Dec-25 11:55 PM
 */
@Data
public class PurchaseItemResDto {
    private Long id;
    private String itemName;
    private String unitName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}

