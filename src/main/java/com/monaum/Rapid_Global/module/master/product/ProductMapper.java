package com.monaum.Rapid_Global.module.master.product;

import com.monaum.Rapid_Global.module.master.company.Company;
import com.monaum.Rapid_Global.module.master.unit.Unit;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

//    // CreateProductReqDto → Product
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "productType", expression = "java(com.monaum.Rapid_Global.module.master.product.ProductType.valueOf(dto.getProductType()))")
//    Product toEntity(CreateProductReqDto dto, Unit unit, Company company);
//
//     UpdateProductReqDto → existing Product
//    @Mapping(target = "unit", source = "unit")
//    @Mapping(target = "productType", expression = "java(com.monaum.Rapid_Global.module.master.product.ProductType.valueOf(dto.getProductType()))")
//    void toEntity(UpdateProductReqDto dto, @MappingTarget Product product, Unit unit, Company company);
//
//     Product → ProductResDto
//    @Mapping(target = "unitId", source = "unit.id")
//    @Mapping(target = "unitName", source = "unit.name")
//    @Mapping(target = "productType", source = "productType")
//    ProductResDto toDto(Product product);
//
//    List<ProductResDto> toDtoList(List<Product> products);
}
