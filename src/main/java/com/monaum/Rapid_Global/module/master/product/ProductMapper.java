package com.monaum.Rapid_Global.module.master.product;

import com.monaum.Rapid_Global.module.master.company.Company;
import com.monaum.Rapid_Global.module.master.unit.Unit;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unit", ignore = true)
    @Mapping(target = "active", constant = "true")
    Product toEntity(ProductReqDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unit", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Product entity, ProductReqDto dto);

    @Mapping(target = "unitId", source = "unit.id")
    @Mapping(target = "unitName", source = "unit.name")
    @Mapping(target = "status", source = "active")
    ProductResDto toDto(Product product);

}
