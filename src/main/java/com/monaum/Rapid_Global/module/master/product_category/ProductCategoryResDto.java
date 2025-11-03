package com.monaum.Rapid_Global.module.master.product_category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryResDto {

    private Long id;

    private String name;

    private String code;

    private String description;

    private Boolean status;

    private Integer sqn;

}
