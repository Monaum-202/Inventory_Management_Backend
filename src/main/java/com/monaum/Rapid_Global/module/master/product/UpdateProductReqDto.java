package com.monaum.Rapid_Global.module.master.product;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductReqDto {

    @NotNull(message = "Product ID is required.")
    private Long id;

    @NotBlank(message = "Product name is required.")
    @Size(max = 50)
    private String name;

    @NotBlank(message = "Product type is required.")
    private String productType;

    @Size(max = 255)
    private String description;

    @NotNull(message = "Unit ID is required.")
    private Long unitId;

    private Integer sortingOrder;

    @DecimalMin(value = "0.00", inclusive = false, message = "Price must be greater than 0.")
    private BigDecimal pricePerUnit;

    @NotNull(message = "Company ID is required.")
    private Long companyId;

    private Boolean status;
}
