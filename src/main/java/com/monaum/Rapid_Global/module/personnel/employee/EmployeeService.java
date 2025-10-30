package com.monaum.Rapid_Global.module.personnel.employee;

import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.util.PaginationUtil;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import com.monaum.Rapid_Global.util.response.CustomPageResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Monaum Hossain
 */

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private static final Logger log = LogManager.getLogger(EmployeeService.class);
    @Autowired
    private EmployeeRepo employeeRepo;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired EmployeeMap employeeMap;

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> createEmployee(EmployeeReqDto dto) {

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

    public ResponseEntity<BaseApiResponseDTO<?>> getAllActive(Boolean status,Pageable pageable) {
        Page<EmployeeResDto> employees = employeeRepo.findAllByStatus(status,pageable).map(employeeMapper::toDto);
        CustomPageResponseDTO<EmployeeResDto> paginatedResponse = PaginationUtil.buildPageResponse(employees, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    public ResponseEntity<BaseApiResponseDTO<?>> delete(Long id) throws CustomException {
        Employee employee = employeeRepo.findById(id).orElseThrow(()-> new CustomException("Employee not found", HttpStatus.NOT_FOUND));
        employeeRepo.delete(employee);

        return ResponseUtils.SuccessResponse("Employee deleted successfully", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> updateEmployee(Long id, EmployeeReqDto dto) {
        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // Update only if value is provided (null checks)
        if (dto.getSalary() != null) employee.setSalary(dto.getSalary());
        if (dto.getJoiningDate() != null) employee.setJoiningDate(dto.getJoiningDate());
        if (dto.getEmail() != null) employee.setEmail(dto.getEmail());
        if (dto.getPhone() != null) employee.setPhone(dto.getPhone());
        if (dto.getName() != null) employee.setName(dto.getName());


        employeeRepo.save(employee);

        return ResponseUtils.SuccessResponseWithData("Employee updated successfully", employeeMapper.toDto(employee));
    }




//    public ResponseEntity<BaseApiResponseDTO<?>> statusUpdate(Long id) {
//        Employee employee = employeeRepo.getReferenceById(id);
//
//        employee.setStatus(!employee.isStatus());
//        employeeRepo.save(employee);
//
//        return ResponseUtils.SuccessResponseWithData(employeeMapper.toDto(employee));
//    }
}
