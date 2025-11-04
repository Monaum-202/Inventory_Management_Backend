package com.monaum.Rapid_Global.module.master.product_category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductCategoryReqDto {

    @NotNull(message = "Product_category ID is required.")
    private Long id;

    @NotBlank(message = "Product_category name required.")
    private String name;

    private String description;

    private Boolean status;


}
