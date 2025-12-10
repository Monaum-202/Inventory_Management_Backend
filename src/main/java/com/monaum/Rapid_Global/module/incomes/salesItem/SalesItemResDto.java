package com.monaum.Rapid_Global.module.incomes.salesItem;

import lombok.Data;

@Data
public class SalesItemResDto {

    private Long id;
    private String itemName;
    private String unitName;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
}
