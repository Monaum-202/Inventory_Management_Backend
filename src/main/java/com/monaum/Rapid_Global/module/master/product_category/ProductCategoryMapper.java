package com.monaum.Rapid_Global.module.master.product_category;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {

    ProductCategory toEntity(ProductCategoryReqDto dto);

    ProductCategoryResDto toDTO(ProductCategory entity);

    void toEntity(ProductCategoryReqDto dto, @MappingTarget ProductCategory productCategory);


}
