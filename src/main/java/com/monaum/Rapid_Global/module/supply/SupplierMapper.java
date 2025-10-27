package com.monaum.Rapid_Global.module.supply;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    Supplier toEntity(CreateSupplierReqDto dto);

    void toEntity(UpdateSupplierReqDto dto, @MappingTarget Supplier supplier);

    SupplierResDto toDto(Supplier supplier);
}
