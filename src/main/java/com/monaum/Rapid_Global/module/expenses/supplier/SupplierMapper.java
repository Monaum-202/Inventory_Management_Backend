package com.monaum.Rapid_Global.module.expenses.supplier;

import com.monaum.Rapid_Global.module.master.unit.Unit;
import com.monaum.Rapid_Global.module.master.unit.UnitResDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    Supplier toEntity(SupplierReqDto dto);

    SupplierResDto toDTO(Supplier entity);
    void toEntity(SupplierReqDto dto, @MappingTarget Supplier supplier);


}
