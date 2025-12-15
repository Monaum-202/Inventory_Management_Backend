package com.monaum.Rapid_Global.module.personnel.employee;

import com.monaum.Rapid_Global.enums.TransactionType;
import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.module.expenses.expense.Expense;
import com.monaum.Rapid_Global.module.expenses.expense.ExpenseMapper;
import com.monaum.Rapid_Global.module.expenses.expense.ExpenseRepo;
import com.monaum.Rapid_Global.module.master.transectionCategory.TransactionCategory;
import com.monaum.Rapid_Global.module.master.transectionCategory.TransactionCategoryResDto;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 29-Oct-25 9:49 PM
 */

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private static final Logger log = LogManager.getLogger(EmployeeService.class);
    @Autowired private EmployeeRepo employeeRepo;
    @Autowired private EmployeeMapper employeeMapper;
    @Autowired private ExpenseRepo expenseRepo;
    @Autowired private ExpenseMapper expenseMapper;

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(String search) {

        List<Employee> employees;
        if (search != null && !search.isBlank()) {
            employees = employeeRepo.search(search);
        } else {
            employees = employeeRepo.findAll();
        }

        List<EmployeeResDto> dtoList = employees.stream().map(employeeMapper::toDto).toList();

        return ResponseUtils.SuccessResponseWithData(dtoList);
    }


    public ResponseEntity<BaseApiResponseDTO<?>> getAllWithLend(String search) {
        List<Employee> employees = (search != null && !search.isBlank())
                ? employeeRepo.search(search)
                : employeeRepo.findAll();

        Pageable last10 = PageRequest.of(0, 10);

        List<EmployeeResDto> dtoList = employees.stream().map(emp -> {
            EmployeeResDto dto = employeeMapper.toDto(emp);

            List<Expense> lends = expenseRepo.findLastExpenses(emp.getId(), last10);
            dto.setLends(lends.stream().map(expenseMapper::toDto).toList());

            BigDecimal totalLend = expenseRepo.getTotalLends(emp.getId());
            dto.setTotalLend(totalLend);

            return dto;
        }).toList();

        return ResponseUtils.SuccessResponseWithData(dtoList);
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getAllActive(Boolean status) {

        List<Employee> employees = employeeRepo.findAllByActive(status);
        List<EmployeeResDto> employeeResDtos = employees.stream().map(employeeMapper::toDto).toList();

        return ResponseUtils.SuccessResponseWithData("Data fetched successfully.", employeeResDtos);
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getById(Long id) throws CustomException {

        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new CustomException("Employee not found", HttpStatus.NOT_FOUND));

        // Fetch lends & total
        List<Expense> lends = expenseRepo.findByEmployeeId(id);
        BigDecimal totalLend = expenseRepo.getTotalLends(id);

        EmployeeResDto dto = employeeMapper.toDto(employee);

//        dto.setLends(
//                lends.stream().map(expenseMapper::toDto).toList()
//        );
        dto.setTotalLend(totalLend);

        return ResponseUtils.SuccessResponseWithData(dto);
    }


    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(EmployeeReqDto dto) {

        Employee employee = employeeMapper.toEntity(dto);
        employee.setEmployeeId(generateCustomId());
        employee = employeeRepo.save(employee);

        return ResponseUtils.SuccessResponseWithData(employeeMapper.toDto(employee));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> update(Long id, EmployeeReqDto dto) throws CustomException {
        Employee employee = employeeRepo.findById(id).orElseThrow(() -> new CustomException("Employee not found", HttpStatus.NOT_FOUND));

        employeeMapper.toEntityUpdate(dto, employee);
        Employee updated = employeeRepo.save(employee);

        return ResponseUtils.SuccessResponseWithData("Updated Successfully!",employeeMapper.toDto(updated));
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

        return ResponseUtils.SuccessResponseWithData("Employee deleted successfully",employeeMapper.toDto(employee));
    }


    //large Import

    private String generateCustomId() {
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

//test
//
//    public ResponseEntity<BaseApiResponseDTO<?>> getAllTest(String search, Pageable pageable) {
//
//        // =======================
//        // 1. Get paginated employees
//        // =======================
//        Page<Employee> employeePage;
//
//        if (search != null && !search.isBlank()) {
//            employeePage = employeeRepo.search(search, pageable);
//        } else {
//            employeePage = employeeRepo.findAll(pageable);
//        }
//
//        List<Employee> employees = employeePage.getContent();
//
//        if (employees.isEmpty()) {
//            CustomPageResponseDTO<EmployeeResDto> emptyResponse =
//                    PaginationUtil.buildPageResponse(Page.empty(pageable), pageable);
//
//            return ResponseUtils.SuccessResponseWithData(emptyResponse);
//        }
//
//        // Extract employee IDs
//        List<Long> employeeIds = employees.stream().map(Employee::getId).toList();
//
//        // =======================
//        // 2. Fetch lends for all employees (1 query)
//        // =======================
//        Pageable limit = PageRequest.of(0, 15);
//        List<Expense> last15 = expenseRepo.findLast15ByEmployeeIds(employeeIds, limit).getContent();
//
//        // Group by employeeId
//        Map<Long, List<Expense>> lendsByEmpId =
//                last15.stream().collect(Collectors.groupingBy(e -> e.getEmployee().getId()));
//
//        // =======================
//        // 3. Fetch total lends grouped (1 query)
//        // =======================
//        Map<Long, BigDecimal> totalLendMap = new HashMap<>();
//
//        List<Object[]> totalLendData = expenseRepo.getTotalLendsByEmployeeIds(employeeIds);
//        for (Object[] row : totalLendData) {
//            Long empId = (Long) row[0];
//            BigDecimal total = (BigDecimal) row[1];
//            totalLendMap.put(empId, total);
//        }
//
//        // =======================
//        // 4. Build final DTO page
//        // =======================
//        Page<EmployeeResDto> dtoPage = employeePage.map(emp -> {
//
//            EmployeeResDto dto = employeeMapper.toDto(emp);
//
//            // Set lends list
////            List<Expense> lends = lendsByEmpId.getOrDefault(emp.getId(), List.of());
////            dto.setLends(
////                    lends.stream().map(expenseMapper::toDto).toList()
////            );
//
//            // Set total lend
//            dto.setTotalLend(totalLendMap.getOrDefault(emp.getId(), BigDecimal.ZERO));
//
//            return dto;
//        });
//
//        CustomPageResponseDTO<EmployeeResDto> paginatedResponse =
//                PaginationUtil.buildPageResponse(dtoPage, pageable);
//
//        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
//    }




}
