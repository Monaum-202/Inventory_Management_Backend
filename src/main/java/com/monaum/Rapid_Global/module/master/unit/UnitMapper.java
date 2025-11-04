package com.monaum.Rapid_Global.module.master.unit;

import com.monaum.Rapid_Global.module.master.product_category.ProductCategory;
import com.monaum.Rapid_Global.module.master.product_category.ProductCategoryReqDto;
import com.monaum.Rapid_Global.module.master.product_category.ProductCategoryResDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UnitMapper {

    Unit toEntity(UnitReqDto dto);

    UnitResDto toDTO(Unit entity);

    void toEntity(ProductCategoryReqDto dto, @MappingTarget ProductCategory productCategory);



}
