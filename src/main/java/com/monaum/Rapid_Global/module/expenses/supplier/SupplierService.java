package com.monaum.Rapid_Global.module.expenses.supplier;

import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.module.expenses.expense.ExpenseRepo;
import com.monaum.Rapid_Global.module.expenses.purchase.PurchaseRepo;
import com.monaum.Rapid_Global.module.incomes.customer.Customer;
import com.monaum.Rapid_Global.module.incomes.customer.CustomerResDto;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SupplierService {
    @Autowired private SupplierRepo repo;
    @Autowired private  SupplierMapper mapper;
    @Autowired private ExpenseRepo expenseRepo;
    @Autowired private PurchaseRepo purchaseRepo;

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(String search, Pageable pageable){

        Page<SupplierResDto> suppliers;
        if (search != null && !search.isBlank()) {
            suppliers = repo.search(search, pageable).map(this::buildCustomerResponse);
        } else {
            suppliers = repo.findAll(pageable).map(this::buildCustomerResponse);
        }

        CustomPageResponseDTO<SupplierResDto> paginatedResponse = PaginationUtil.buildPageResponse(suppliers, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>>create(SupplierReqDto dto ) {
        Supplier entity = mapper.toEntity(dto);
        entity = repo.save(entity);

        return ResponseUtils.SuccessResponseWithData(mapper.toDTO(entity));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getById(Long id) throws CustomException{
            Supplier supplier = repo.findById(id).orElseThrow(()-> new CustomException("Supplier not found", HttpStatus.NOT_FOUND));

            return ResponseUtils.SuccessResponseWithData(mapper.toDTO(supplier));
    }

    public  ResponseEntity<BaseApiResponseDTO<?>> getByPhone(String phone) throws CustomException {

        Supplier supplier = repo.findByPhone(phone).orElseThrow(()-> new CustomException("Customer not found", HttpStatus.NOT_FOUND));

        return ResponseUtils.SuccessResponseWithData(mapper.toDTO(supplier));
    }

    //helper methods
    private SupplierResDto buildCustomerResponse(Supplier supplier) {
        SupplierResDto dto = mapper.toDTO(supplier);

        BigDecimal paid = BigDecimal.valueOf(
                Optional.ofNullable(
                        expenseRepo.getTotalTransaction(supplier.getId())
                ).orElse(0.0)
        );
        dto.setTotalTransaction(paid);

        List<BigDecimal> perSaleTotals =
                Optional.ofNullable(
                        purchaseRepo.calculatePerSaleTotalsByCustomer(supplier.getId())
                ).orElse(List.of());

        BigDecimal totalOrder = BigDecimal.valueOf(
                perSaleTotals.stream()
                        .filter(Objects::nonNull)
                        .mapToDouble(BigDecimal::doubleValue)
                        .sum()
        );
        dto.setTotalDue(totalOrder.subtract(paid));
        return dto;
    }
}
