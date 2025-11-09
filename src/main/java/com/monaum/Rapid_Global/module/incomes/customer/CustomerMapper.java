package com.monaum.Rapid_Global.module.incomes.customer;
import com.monaum.Rapid_Global.module.master.unit.Unit;
import com.monaum.Rapid_Global.module.master.unit.UnitResDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toEntity(CustomerReqDto dto);
    CustomerResDto toDTO(Customer entity);
    void toEntity(CustomerReqDto dto, @MappingTarget Customer customer);



}
