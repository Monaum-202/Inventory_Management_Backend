package com.monaum.Rapid_Global.module.master.product_category;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductCategoryReqDto {

    @NotBlank(message = "Company name required.")
    private String name;

    private String code;

    private String description;

    private Boolean status = true;


}
