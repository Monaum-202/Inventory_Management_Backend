package com.monaum.Rapid_Global.module.incomes.salesItem;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SalesItemReqDto {

    @NotBlank(message = "Item name is required")
    private String itemName;

    private String unitName;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;

    @NotNull(message = "Unit price is required")
    @PositiveOrZero(message = "Unit price must be zero or positive")
    private Double unitPrice;

    @NotNull(message = "Total price is required")
    @PositiveOrZero(message = "Total price must be zero or positive")
    private Double totalPrice;
}
