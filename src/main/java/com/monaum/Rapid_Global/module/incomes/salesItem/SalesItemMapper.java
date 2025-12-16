package com.monaum.Rapid_Global.module.incomes.salesItem;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SalesItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sales", ignore = true)
    SalesItem toEntity(SalesItemReqDto dto);

    SalesItemResDto toResDto(SalesItem entity);
}
