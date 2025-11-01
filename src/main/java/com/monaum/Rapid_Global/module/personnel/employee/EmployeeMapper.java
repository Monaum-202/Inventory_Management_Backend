package com.monaum.Rapid_Global.module.personnel.employee;

import org.mapstruct.*;

/**
 * Monaum Hossain
 * @since Oct 21, 2025
 */

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "true")
    @Mapping(target = "lends", ignore = true)
    Employee toEntity(EmployeeReqDto dto);

    EmployeeResDto toDto(Employee entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toEntityUpdate(EmployeeReqDto dto, @MappingTarget Employee employee);
}

