package com.monaum.Rapid_Global.module.incomes.estimate;

import lombok.Data;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 12-Dec-25
 */

@Data
public class EstimateItemResDto {

    private Long id;
    private String itemName;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
    private String description;
}