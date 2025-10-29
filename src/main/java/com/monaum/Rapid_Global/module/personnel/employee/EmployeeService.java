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
    public ResponseEntity<BaseApiResponseDTO<?>> update(Long id, UpdateEmployeeReqDto dto) {
        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new CustomException("Employee not found with ID: " + id, HttpStatus.NOT_FOUND));

        // Unique checks
        if (employeeRepo.existsByPhoneAndIdNot(dto.getPhone(), id)) {
            throw new CustomException("Phone number already exists for another employee.", HttpStatus.BAD_REQUEST);
        }

        if (employeeRepo.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new CustomException("Email address already exists for another employee.", HttpStatus.BAD_REQUEST);
        }

        // Map DTO → existing entity
        employeeMapper.toEntity(dto, employee);

        try {
            Employee updated = employeeRepo.save(employee);
            return ResponseUtils.SuccessResponseWithData(employeeMapper.toDto(updated));
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null && root != root.getCause()) {
                root = root.getCause();
            }
            root.printStackTrace();
            throw new CustomException("Employee update failed: " + root.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<BaseApiResponseDTO<?>> statusUpdate(Long id) {
        Employee employee = employeeRepo.getReferenceById(id);

        employee.setStatus(!employee.isStatus());
        employeeRepo.save(employee);

        return ResponseUtils.SuccessResponseWithData(employeeMapper.toDto(employee));
    }
}
