package com.monaum.Rapid_Global.module.personnel.employee;

import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.util.PaginationUtil;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import com.monaum.Rapid_Global.util.response.CustomPageResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Monaum Hossain
 */

@Service
@RequiredArgsConstructor
public class EmployeeService {
    @Autowired
    private EmployeeRepo employeeRepo;
    @Autowired
    private EmployeeMapper employeeMapper;

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> createEmployee(CreateEmployeeReqDto dto) {

        Employee employee = employeeMapper.toEntity(dto);
        employee = employeeRepo.save(employee);

        return ResponseUtils.SuccessResponseWithData(employeeMapper.toDto(employee));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getById(Long id) throws CustomException {

        Employee employee = employeeRepo.findById(id).orElseThrow(()-> new CustomException("Employee not found", HttpStatus.NOT_FOUND));

        return ResponseUtils.SuccessResponseWithData(employeeMapper.toDto(employee));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(Pageable pageable) {

        Page<EmployeeResDto> employees = employeeRepo.findAll(pageable).map(employeeMapper::toDto);
        CustomPageResponseDTO<EmployeeResDto> paginatedResponse = PaginationUtil.buildPageResponse(employees, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getActiveEmployees(Boolean status,Pageable pageable) {
        Page<EmployeeResDto> employees = employeeRepo.findAllByStatus(status,pageable).map(employeeMapper::toDto);
        CustomPageResponseDTO<EmployeeResDto> paginatedResponse = PaginationUtil.buildPageResponse(employees, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    public ResponseEntity<BaseApiResponseDTO<?>> deleteById(Long id) throws CustomException {

        Employee employee = employeeRepo.findById(id).orElseThrow(()-> new CustomException("Employee not found", HttpStatus.NOT_FOUND));
        employeeRepo.delete(employee);

        return ResponseUtils.SuccessResponse("Employee deleted successfully", HttpStatus.OK);
    }

    public ResponseEntity<BaseApiResponseDTO<?>> updateEmployee(UpdateEmployeeReqDto dto) {
        Employee employee = employeeRepo.getReferenceById(dto.getId());

        employeeMapper.toEntity(dto, employee);
        Employee updated = employeeRepo.save(employee);

        return ResponseUtils.SuccessResponseWithData(employeeMapper.toDto(updated));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> statusUpdate(Long id) {

        Employee employee = employeeRepo.getReferenceById(id);

        employee.setStatus(!employee.isStatus());
        employeeRepo.save(employee);

        return ResponseUtils.SuccessResponseWithData(employeeMapper.toDto(employee));
    }
}
