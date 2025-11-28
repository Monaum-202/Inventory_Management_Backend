package com.monaum.Rapid_Global.module.incomes.income;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface IncomeMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "incomeCategory", ignore = true)
    @Mapping(target = "paymentMethod", ignore = true)
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    Income toEntity(IncomeReqDTO dto);

    @Mapping(target = "categoryId", source = "incomeCategory.id")
    @Mapping(target = "categoryName", source = "incomeCategory.name")
    @Mapping(target = "paymentMethodId", source = "paymentMethod.id")
    @Mapping(target = "paymentMethodName", source = "paymentMethod.name")
    @Mapping(target = "approvedByName", source = "approvedBy.fullName")
    @Mapping(target = "createdBy", source = "createdBy.fullName")
    IncomeResDto toDto(Income entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "incomeId", ignore = true)
    @Mapping(target = "incomeCategory", ignore = true)
    @Mapping(target = "paymentMethod", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntityFromDto(IncomeReqDTO dto, @MappingTarget Income entity);

}
