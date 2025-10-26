package com.monaum.Rapid_Global.module.master.product;

import com.monaum.Rapid_Global.module.master.company.Company;
import com.monaum.Rapid_Global.module.master.unit.Unit;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // CreateProductReqDto → Product
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unit", source = "unit")
    @Mapping(target = "company", source = "company")
    @Mapping(target = "productType", expression = "java(com.monaum.Rapid_Global.module.master.product.ProductType.valueOf(dto.getProductType()))")
    Product toEntity(CreateProductReqDto dto, Unit unit, Company company);

    // UpdateProductReqDto → existing Product
    @Mapping(target = "unit", source = "unit")
    @Mapping(target = "company", source = "company")
    @Mapping(target = "productType", expression = "java(com.monaum.Rapid_Global.module.master.product.ProductType.valueOf(dto.getProductType()))")
    void toEntity(UpdateProductReqDto dto, @MappingTarget Product product, Unit unit, Company company);

    // Product → ProductResDto
    @Mapping(target = "unitId", source = "unit.id")
    @Mapping(target = "unitName", source = "unit.name")
    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "companyName", source = "company.name")
    @Mapping(target = "productType", source = "productType")
    ProductDto toDto(Product product);

    List<ProductDto> toDtoList(List<Product> products);
}
