package com.monaum.Rapid_Global.module.incomes.customer;
import com.monaum.Rapid_Global.module.master.unit.Unit;
import com.monaum.Rapid_Global.module.master.unit.UnitResDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "totalTransaction", ignore = true)
    Customer toEntity(CustomerReqDto dto);
    CustomerResDto toDTO(Customer entity);
    void toEntity(CustomerReqDto dto, @MappingTarget Customer customer);



}
