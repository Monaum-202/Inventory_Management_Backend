package com.monaum.Rapid_Global.module.personnel.employee;

import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Monaum Hossain
 */

@Component
public class EmployeeMap {

    // Convert DTO to Entity
    public Employee toEntity(EmployeeReqDto dto) {
        if (dto == null) return null;

        Employee employee = new Employee();
        employee.setName(dto.getName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setSalary(dto.getSalary());
        employee.setJoiningDate(dto.getJoiningDate());
        return employee;
    }

    // Convert Entity to DTO
    public EmployeeReqDto toDto(Employee employee) {
        if (employee == null) return null;

        return EmployeeReqDto.builder()
                .name(employee.getName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .salary(employee.getSalary())
                .joiningDate(employee.getJoiningDate())
                .build();
    }

    // Update existing entity from DTO
    public void updateEntityFromDto(EmployeeReqDto dto, Employee entity) {
        if (dto == null || entity == null) return;

        if (Objects.nonNull(dto.getName())) {
            entity.setName(dto.getName());
        }
        if (Objects.nonNull(dto.getEmail())) {
            entity.setEmail(dto.getEmail());
        }
        if (Objects.nonNull(dto.getPhone())) {
            entity.setPhone(dto.getPhone());
        }
        if (Objects.nonNull(dto.getSalary())) {
            entity.setSalary(dto.getSalary());
        }
        if (Objects.nonNull(dto.getJoiningDate())) {
            entity.setJoiningDate(dto.getJoiningDate());
        }
    }
}
