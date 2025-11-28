package com.monaum.Rapid_Global.module.incomes.income;

import com.monaum.Rapid_Global.config.SecurityUtil;
import com.monaum.Rapid_Global.enums.Status;
import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.module.master.paymentMethod.PaymentMethod;
import com.monaum.Rapid_Global.module.master.paymentMethod.RepoPaymentMethod;
import com.monaum.Rapid_Global.module.master.transectionCategory.TransactionCategory;
import com.monaum.Rapid_Global.module.master.transectionCategory.TransactionCategoryRepo;
import com.monaum.Rapid_Global.util.PaginationUtil;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import com.monaum.Rapid_Global.util.response.CustomPageResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class IncomeService {

    @Autowired private IncomeRepo incomeRepo;
    @Autowired private IncomeMapper  incomeMapper;

    @Autowired private TransactionCategoryRepo incomeCategoryRepo;
    @Autowired private RepoPaymentMethod paymentMethodRepo;

    @Autowired private SecurityUtil securityUtil;

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(String search, Pageable pageable){
        Page<IncomeResDto> incomes;
        if (search != null && !search.isBlank()) {
            incomes = incomeRepo.search(search, pageable).map(incomeMapper::toDto);
        }else {
            incomes = incomeRepo.findAll(pageable).map(incomeMapper::toDto);
        }

        CustomPageResponseDTO<IncomeResDto> paginatedResponse = PaginationUtil.buildPageResponse(incomes, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(IncomeReqDTO dto){
        TransactionCategory incomeCategory = incomeCategoryRepo.findById(dto.getIncomeCategory()).orElseThrow(() -> new CustomException("Income Category not found with id: " + dto.getIncomeCategory(), HttpStatus.NOT_FOUND));
        PaymentMethod paymentMethod = paymentMethodRepo.findById(dto.getPaymentMethodId()).orElseThrow(() -> new CustomException("Payment Method not found with id: " + dto.getPaymentMethodId(), HttpStatus.NOT_FOUND));

        Income income = incomeMapper.toEntity(dto);
        income.setIncomeCategory(incomeCategory);
        income.setPaymentMethod(paymentMethod);
        income.setIncomeId(generateIncomeId());
        income.setStatus(Status.APPROVED);
        income.setApprovedBy(securityUtil.getAuthenticatedUser());

        incomeRepo.save(income);

        return  ResponseUtils.SuccessResponseWithData(incomeMapper.toDto(income));
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> update(Long id, IncomeReqDTO dto) throws CustomException{
        Income income = incomeRepo.findById(id).orElseThrow(() -> new CustomException("Income not found with id: " + id, HttpStatus.NOT_FOUND));
        TransactionCategory incomeCategory = incomeCategoryRepo.findById(dto.getIncomeCategory()).orElseThrow(() -> new CustomException("Income Category not found with id: " + dto.getIncomeCategory(), HttpStatus.NOT_FOUND));
        PaymentMethod paymentMethod = paymentMethodRepo.findById(dto.getPaymentMethodId()).orElseThrow(() -> new CustomException("Payment Method not found with id: " + dto.getPaymentMethodId(), HttpStatus.NOT_FOUND));

        incomeMapper.updateEntityFromDto(dto,income);
        income.setIncomeCategory(incomeCategory);
        income.setPaymentMethod(paymentMethod);
        income.setIncomeId(generateIncomeId());
        income.setStatus(Status.APPROVED);
        income.setApprovedBy(securityUtil.getAuthenticatedUser());
        Income updated = incomeRepo.save(income);

        return ResponseUtils.SuccessResponseWithData(incomeMapper.toDto(updated));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> updateStatus(Long id, Status status, String cancelReason) {
        Income income = incomeRepo.findById(id).orElseThrow(() -> new CustomException("Income not found with id: " + id, HttpStatus.NOT_FOUND));

        income.setStatus(status);
        income.setApprovedBy(securityUtil.getAuthenticatedUser());
        if (cancelReason!=null){income.setCancelReason(cancelReason);}

        incomeRepo.save(income);

        return ResponseUtils.SuccessResponse("Approved successfully!", HttpStatus.OK);
    }

    public ResponseEntity<BaseApiResponseDTO<?>> delete(Long id){
        Income income = incomeRepo.findById(id).orElseThrow(() -> new CustomException("Income not found with id: " + id, HttpStatus.NOT_FOUND));

        incomeRepo.delete(income);

        return ResponseUtils.SuccessResponse("Income has been deleted", HttpStatus.OK);
    }

    @Transactional
    public String generateIncomeId() {
        String lastId = incomeRepo.findLastIncomeIdForUpdate();

        String year = String.valueOf(LocalDate.now().getYear()).substring(2);  // YY
        String month = String.format("%02d", LocalDate.now().getMonthValue()); // MM

        // If no previous ID → start with 001
        if (lastId == null) {
            return "INC" + year + month + "001";
        }

        // Extract last ID's year and month
        String lastYear = lastId.substring(3, 5);  // YY from EXPYYMM###
        String lastMonth = lastId.substring(5, 7); // MM from EXPYYMM###

        // If month OR year changed → reset counter to 001
        if (!lastYear.equals(year) || !lastMonth.equals(month)) {
            return "INC" + year + month + "001";
        }

        // Otherwise, increment existing number
        int number = Integer.parseInt(lastId.substring(7)); // last 3 digits
        number++;

        return "INC" + year + month + String.format("%03d", number);
    }

}
