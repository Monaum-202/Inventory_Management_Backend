package com.monaum.Rapid_Global.module.incomes.sales;

import com.monaum.Rapid_Global.module.incomes.salesItem.SalesItemMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { SalesItemMapper.class })
public interface SalesMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "invoiceNo", ignore = true)
    @Mapping(target = "items", source = "items")
    Sales toEntity(SalesReqDTO dto);

    SalesResDto toResDto(Sales entity);
}
