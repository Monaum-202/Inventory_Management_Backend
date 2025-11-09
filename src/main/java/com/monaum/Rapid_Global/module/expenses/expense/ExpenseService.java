package com.monaum.Rapid_Global.module.expenses.expense;

import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.module.expenses.expense_category.ExpenseCategory;
import com.monaum.Rapid_Global.module.expenses.expense_category.ExpenseCategoryRepo;
import com.monaum.Rapid_Global.module.master.paymentMethod.PaymentMethod;
import com.monaum.Rapid_Global.module.master.paymentMethod.RepoPaymentMethod;
import com.monaum.Rapid_Global.module.personnel.employee.Employee;
import com.monaum.Rapid_Global.module.personnel.employee.EmployeeRepo;
import com.monaum.Rapid_Global.util.PaginationUtil;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import com.monaum.Rapid_Global.util.response.CustomPageResponseDTO;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 25-Oct-25 10:52 PM
 */

@Service
public class ExpenseService {

    @Autowired private ExpenseRepo expenseRepo;
    @Autowired private ExpenseMapper expenseMapper;

    @Autowired private ExpenseCategoryRepo expenseCategoryRepo;
    @Autowired private RepoPaymentMethod paymentMethodRepo;
    @Autowired private EmployeeRepo employeeRepo;

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(Pageable pageable){

        Page<ExpenseResDto> expenses = expenseRepo.findAll(pageable).map(expenseMapper ::toDto);
        CustomPageResponseDTO<ExpenseResDto> paginatedResponse = PaginationUtil.buildPageResponse(expenses, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getById(Long id){
        Expense expense = expenseRepo.findById(id).orElseThrow(() -> new CustomException("Expense not found with id: " + id , HttpStatus.NOT_FOUND));

        return ResponseUtils.SuccessResponseWithData(expenseMapper.toDto(expense));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> create(ExpenseReqDTO dto){
        ExpenseCategory expenseCategory = expenseCategoryRepo.findById(dto.getExpenseCategory()).orElseThrow(() -> new CustomException("Expense Category not found with id: " + dto.getExpenseCategory(), HttpStatus.NOT_FOUND));
        PaymentMethod paymentMethod = paymentMethodRepo.findById(dto.getPaymentMethodId()).orElseThrow(() -> new CustomException("Payment Method not found with id: " + dto.getPaymentMethodId(), HttpStatus.NOT_FOUND));
        Employee employee = employeeRepo.findById(dto.getEmployeeId()).orElseThrow(() -> new  CustomException("Employee not found with id: " + dto.getEmployeeId(), HttpStatus.NOT_FOUND));

        Expense expense = expenseMapper.toEntity(dto);
        expense.setExpenseCategory(expenseCategory);
        expense.setPaymentMethod(paymentMethod);
        expense.setEmployee(employee);
        expenseRepo.save(expense);

        return  ResponseUtils.SuccessResponseWithData(expenseMapper.toDto(expense));
    }

}
