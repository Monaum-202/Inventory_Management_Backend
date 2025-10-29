package com.monaum.Rapid_Global.module.personnel.employee;

import com.monaum.Rapid_Global.module.expenses.expense.ExpenseMapper;
import com.monaum.Rapid_Global.module.master.company.Company;
import org.mapstruct.*;
import java.util.List;

/**
 * Monaum Hossain
 * @since Oct 21, 2025
 */

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ExpenseMapper.class})
public interface EmployeeMapper {


    EmployeeResDto toDto(Employee employee);

    List<EmployeeResDto> toDtoList(List<Employee> employees);


    Employee toEntity(CreateEmployeeReqDto dto);

    void toEntity(UpdateEmployeeReqDto dto, @MappingTarget Employee employee);
}
