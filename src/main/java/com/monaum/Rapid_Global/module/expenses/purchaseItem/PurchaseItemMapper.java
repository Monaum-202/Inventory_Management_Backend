package com.monaum.Rapid_Global.module.expenses.purchaseItem;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchase", ignore = true)
    PurchaseItem toEntity(PurchaseItemReqDto dto);

    PurchaseItemResDto toResDto(PurchaseItem entity);
}