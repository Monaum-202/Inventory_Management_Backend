package com.monaum.Rapid_Global.module.personnel.employee;

import org.mapstruct.*;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 29-Oct-25 9:49 PM
 */

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
//    @Mapping(target = "lends", ignore = true)
    Employee toEntity(EmployeeReqDto dto);

//    @Mapping(target = "lends", ignore = true)
    EmployeeResDto toDto(Employee entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void toEntityUpdate(EmployeeReqDto dto, @MappingTarget Employee employee);
}

