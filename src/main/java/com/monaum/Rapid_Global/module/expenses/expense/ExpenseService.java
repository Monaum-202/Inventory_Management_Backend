package com.monaum.Rapid_Global.module.expenses.expense;

import com.monaum.Rapid_Global.config.SecurityUtil;
import com.monaum.Rapid_Global.enums.Status;
import com.monaum.Rapid_Global.exception.CustomException;

import com.monaum.Rapid_Global.module.master.paymentMethod.PaymentMethod;
import com.monaum.Rapid_Global.module.master.paymentMethod.RepoPaymentMethod;
import com.monaum.Rapid_Global.module.master.transectionCategory.TransectionCategory;
import com.monaum.Rapid_Global.module.master.transectionCategory.TransectionCategoryRepo;
import com.monaum.Rapid_Global.module.personnel.employee.Employee;
import com.monaum.Rapid_Global.module.personnel.employee.EmployeeRepo;
import com.monaum.Rapid_Global.module.expenses.expense.ExpenseReqDTO;
import com.monaum.Rapid_Global.module.personnel.user.User;
import com.monaum.Rapid_Global.util.PaginationUtil;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import com.monaum.Rapid_Global.util.response.CustomPageResponseDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 25-Oct-25 10:52 PM
 */

@Service
public class ExpenseService {

    @Autowired private ExpenseRepo expenseRepo;
    @Autowired private ExpenseMapper expenseMapper;

    @Autowired private TransectionCategoryRepo expenseCategoryRepo;
    @Autowired private RepoPaymentMethod paymentMethodRepo;
    @Autowired private EmployeeRepo employeeRepo;
    @Autowired private SecurityUtil securityUtil;

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(String search, Pageable pageable){
        Page<ExpenseResDto> expenses;
        if (search != null && !search.isBlank()) {
            expenses = expenseRepo.search(search, pageable).map(expenseMapper::toDto);
        }else {
            expenses = expenseRepo.findAll(pageable).map(expenseMapper::toDto);
        }

        CustomPageResponseDTO<ExpenseResDto> paginatedResponse = PaginationUtil.buildPageResponse(expenses, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getById(Long id){
        Expense expense = expenseRepo.findById(id).orElseThrow(() -> new CustomException("Expense not found with id: " + id , HttpStatus.NOT_FOUND));

        return ResponseUtils.SuccessResponseWithData(expenseMapper.toDto(expense));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> create(ExpenseReqDTO dto){
        TransectionCategory expenseCategory = expenseCategoryRepo.findById(dto.getExpenseCategory()).orElseThrow(() -> new CustomException("Expense Category not found with id: " + dto.getExpenseCategory(), HttpStatus.NOT_FOUND));
        PaymentMethod paymentMethod = paymentMethodRepo.findById(dto.getPaymentMethodId()).orElseThrow(() -> new CustomException("Payment Method not found with id: " + dto.getPaymentMethodId(), HttpStatus.NOT_FOUND));
        Employee employee = employeeRepo.findById(dto.getEmployeeId()).orElseThrow(() -> new CustomException("Employee not found with id: " + dto.getEmployeeId(), HttpStatus.NOT_FOUND));

        Expense expense = expenseMapper.toEntity(dto);
        expense.setExpenseCategory(expenseCategory);
        expense.setPaymentMethod(paymentMethod);
        expense.setEmployee(employee);
        expense.setExpenseId(generateExpenseId());
        if (securityUtil.getAuthenticatedUser().getRole().getId()==1){
            expense.setStatus(Status.APPROVED);
            expense.setApprovedBy(securityUtil.getAuthenticatedUser());
        }
        expenseRepo.save(expense);

        return  ResponseUtils.SuccessResponseWithData(expenseMapper.toDto(expense));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> update(Long id, ExpenseReqDTO dto) throws CustomException{
        Expense expense = expenseRepo.findById(id).orElseThrow(() -> new CustomException("Expense not found with id: " + id, HttpStatus.NOT_FOUND));
        TransectionCategory expenseCategory = expenseCategoryRepo.findById(dto.getExpenseCategory()).orElseThrow(() -> new CustomException("Expense Category not found with id: " + dto.getExpenseCategory(), HttpStatus.NOT_FOUND));
        PaymentMethod paymentMethod = paymentMethodRepo.findById(dto.getPaymentMethodId()).orElseThrow(() -> new CustomException("Payment Method not found with id: " + dto.getPaymentMethodId(), HttpStatus.NOT_FOUND));
        Employee  employee = employeeRepo.findById(dto.getEmployeeId()).orElseThrow(() -> new CustomException("Employee not found with id: " + dto.getEmployeeId(), HttpStatus.NOT_FOUND));

        expenseMapper.updateEntityFromDto(dto,expense);
        expense.setExpenseCategory(expenseCategory);
        expense.setPaymentMethod(paymentMethod);
        expense.setEmployee(employee);
        if (securityUtil.getAuthenticatedUser().getRole().getId()==1){
            expense.setStatus(Status.APPROVED);
            expense.setApprovedBy(securityUtil.getAuthenticatedUser());
        }
        Expense updated = expenseRepo.save(expense);

        return ResponseUtils.SuccessResponseWithData(expenseMapper.toDto(updated));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> delete(Long id){
        Expense expense = expenseRepo.findById(id).orElseThrow(() -> new CustomException("Expense not found with id: " + id, HttpStatus.NOT_FOUND));

        expenseRepo.delete(expense);

        return ResponseUtils.SuccessResponse("Expense has been deleted", HttpStatus.OK);
    }

    public String generateExpenseId() {
        String lastId = expenseRepo.findLastExpenseId(); // EXP25004

        String year = String.valueOf(LocalDate.now().getYear()).substring(2); // 25

        if (lastId == null) {
            return "EXP" + year + "001";
        }

        int number = Integer.parseInt(lastId.substring(5)); // 004
        number++;

        return "EXP" + year + String.format("%03d", number);
    }
}
