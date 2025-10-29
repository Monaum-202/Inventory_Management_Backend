package com.monaum.Rapid_Global.module.incomes.customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toEntity(CreateCustomerReqDto dto);

    void toEntity(UpdateCustomerReqDto dto, @MappingTarget Customer customer);

    Customer toDto(Customer customer);

}
