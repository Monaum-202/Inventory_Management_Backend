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

    // -------------------- ENTITY TO RESPONSE --------------------
    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company.name", target = "companyName")
    EmployeeResDto toDto(Employee employee);

    List<EmployeeResDto> toDtoList(List<Employee> employees);

    // -------------------- CREATE REQUEST TO ENTITY --------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "companyId", target = "company.id")
    @Mapping(target = "lends", ignore = true)
    Employee toEntity(CreateEmployeeReqDto dto, @Context Company company);

    // -------------------- UPDATE REQUEST TO ENTITY --------------------
    @Mapping(source = "companyId", target = "company.id")
    @Mapping(target = "lends", ignore = true)
    void updateEntityFromDto(UpdateEmployeeReqDto dto, @MappingTarget Employee employee);
}
