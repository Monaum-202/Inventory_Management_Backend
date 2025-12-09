package com.monaum.Rapid_Global.module.master.product;

import com.monaum.Rapid_Global.enums.ProductType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReqDto {

    @NotBlank(message = "Product name is required.")
    @Size(max = 50, message = "Product name can be at most 50 characters long.")
    private String name;

    private ProductType productType;

    @Size(max = 255, message = "Description can be at most 255 characters long.")
    private String description;

    @NotNull(message = "Unit ID is required.")
    private Long unitId;

    private Integer sortingOrder;

    @DecimalMin(value = "0.00", inclusive = false, message = "Price must be greater than 0.")
    private BigDecimal pricePerUnit;
}

