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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * @since 29-Oct-25 9:49 PM
 */

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private static final Logger log = LogManager.getLogger(EmployeeService.class);
    @Autowired private EmployeeRepo employeeRepo;
    @Autowired private EmployeeMapper employeeMapper;

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(Pageable pageable) {

        Page<EmployeeResDto> employees = employeeRepo.findAll(pageable).map(employeeMapper::toDto);
        CustomPageResponseDTO<EmployeeResDto> paginatedResponse = PaginationUtil.buildPageResponse(employees, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(String search, Pageable pageable) {
        Page<EmployeeResDto> employees;

        if (search != null && !search.isBlank()) {
            employees = employeeRepo.search(search, pageable).map(employeeMapper::toDto);
        } else {
            employees = employeeRepo.findAll(pageable).map(employeeMapper::toDto);
        }

        CustomPageResponseDTO<EmployeeResDto> paginatedResponse = PaginationUtil.buildPageResponse(employees, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }


    public ResponseEntity<BaseApiResponseDTO<?>> getAllActive(Boolean active, Pageable pageable) {

        Page<EmployeeResDto> employees = employeeRepo.findAllByActive(active, pageable).map(employeeMapper::toDto);
        CustomPageResponseDTO<EmployeeResDto> paginatedResponse = PaginationUtil.buildPageResponse(employees, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getById(Long id) throws CustomException {
        Employee employee = employeeRepo.findById(id).orElseThrow(() -> new CustomException("Employee not found", HttpStatus.NOT_FOUND));

        return ResponseUtils.SuccessResponseWithData(employeeMapper.toDto(employee));
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(EmployeeReqDto dto) {

        Employee employee = employeeMapper.toEntity(dto);
        employee.setEmployeeId(generateEmployeeId());
        employee = employeeRepo.save(employee);

        return ResponseUtils.SuccessResponseWithData(employeeMapper.toDto(employee));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> update(Long id, EmployeeReqDto dto) throws CustomException {
        Employee employee = employeeRepo.findById(id).orElseThrow(() -> new CustomException("Employee not found", HttpStatus.NOT_FOUND));

        employeeMapper.toEntityUpdate(dto, employee);
        Employee updated = employeeRepo.save(employee);

        return ResponseUtils.SuccessResponseWithData(employeeMapper.toDto(updated));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> delete(Long id) throws CustomException {
        Employee employee = employeeRepo.findById(id).orElseThrow(() -> new CustomException("Employee not found", HttpStatus.NOT_FOUND));

        employeeRepo.delete(employee);

        return ResponseUtils.SuccessResponse("Employee deleted successfully", HttpStatus.OK);
    }

    public ResponseEntity<BaseApiResponseDTO<?>> activeUpdate(Long id) throws CustomException {
        Employee employee = employeeRepo.findById(id).orElseThrow(() -> new CustomException("Employee not found", HttpStatus.NOT_FOUND));

        employee.setActive(!Boolean.TRUE.equals(employee.getActive()));
        employeeRepo.save(employee);

        return ResponseUtils.SuccessResponseWithData(employeeMapper.toDto(employee));
    }


    //large Import


    private String generateEmployeeId() {
        int year = LocalDate.now().getYear() % 100;
        int serial = 1;

        Optional<Employee> lastEmployee = employeeRepo.findLastEmployee();
        if (lastEmployee.isPresent()) {
            String lastId = lastEmployee.get().getEmployeeId();
            if (lastId != null && lastId.length() >= 8) {
                try {
                    int lastSerial = Integer.parseInt(lastId.substring(5));
                    serial = lastSerial + 1;
                } catch (NumberFormatException ignored) {}
            }
        }

        return String.format("EMP%02d%03d", year, serial);
    }


    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> importEmployees(List<EmployeeReqDto> employeeList) {
        if (employeeList == null || employeeList.isEmpty()) {
            return ResponseUtils.FailedResponse("No data provided for import");
        }

        // Get last employee to continue serial numbering
        Optional<Employee> lastEmployeeOpt = employeeRepo.findLastEmployee();
        int year = LocalDate.now().getYear() % 100; // last 2 digits of year
        int serial = 0;

        if (lastEmployeeOpt.isPresent()) {
            String lastId = lastEmployeeOpt.get().getEmployeeId(); // e.g. EMP250012
            if (lastId != null && lastId.length() >= 8) {
                try {
                    serial = Integer.parseInt(lastId.substring(5)); // "0012"
                } catch (NumberFormatException ignored) {}
            }
        }

        AtomicInteger counter = new AtomicInteger(serial);

        List<Employee> employees = employeeList.stream()
                .map(employeeMapper::toEntity)
                .peek(emp -> {
                    int nextSerial = counter.incrementAndGet();
                    emp.setEmployeeId(String.format("EMP%02d%03d", year, nextSerial));
                })
                .collect(Collectors.toList());

        employeeRepo.saveAll(employees);
        return ResponseUtils.SuccessResponse("Employees imported successfully");
    }


}
