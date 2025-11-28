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
    public String generateIncomeId() {
        String lastId = incomeRepo.findLastIncomeIdForUpdate();

        String year = String.valueOf(LocalDate.now().getYear()).substring(2);

        if (lastId == null) {
            return "EXP" + year + "001";
        }

        int number = Integer.parseInt(lastId.substring(5));
        number++;

        return "INC" + year + String.format("%03d", number);
    }
}
