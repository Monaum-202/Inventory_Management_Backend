package com.monaum.Rapid_Global.module.personnel.employee;

import org.mapstruct.*;

/**
 * Monaum Hossain
 * @since Oct 21, 2025
 */

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    Employee toEntity(EmployeeReqDto dto);

    EmployeeResDto toDto(Employee entity);

}

