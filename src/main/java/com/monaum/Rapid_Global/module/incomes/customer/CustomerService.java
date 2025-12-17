package com.monaum.Rapid_Global.module.incomes.customer;

import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.module.incomes.income.IncomeMapper;
import com.monaum.Rapid_Global.module.incomes.income.IncomeRepo;
import com.monaum.Rapid_Global.module.incomes.sales.SalesRepo;
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
public class CustomerService {

    @Autowired private CustomerRepo repo;
    @Autowired private CustomerMapper mapper;
    @Autowired private IncomeRepo incomeRepo;
    @Autowired private SalesRepo salesRepo;


    public ResponseEntity<BaseApiResponseDTO<?>> getAll(String search, Pageable pageable){

        Page<CustomerResDto> customers;
        if (search != null && !search.isBlank()) {
            customers = repo.search(search, pageable).map(this::buildCustomerResponse);
        } else {
            customers = repo.findAll(pageable).map(this::buildCustomerResponse);
        }

        CustomPageResponseDTO<CustomerResDto> paginatedResponse = PaginationUtil.buildPageResponse(customers, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    public  ResponseEntity<BaseApiResponseDTO<?>> getById(Long id) throws CustomException {

        Customer customer = repo.findById(id).orElseThrow(()-> new CustomException("Customer not found", HttpStatus.NOT_FOUND));

        return ResponseUtils.SuccessResponseWithData(mapper.toDTO(customer));
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(CustomerReqDto dto){

        Customer entity = mapper.toEntity(dto);
        entity = repo.save(entity);

        return ResponseUtils.SuccessResponseWithData(mapper.toDTO(entity));
    }

    public  ResponseEntity<BaseApiResponseDTO<?>> getByPhone(String phone) throws CustomException {

        Customer customer = repo.findByPhone(phone).orElseThrow(()-> new CustomException("Customer not found", HttpStatus.NOT_FOUND));

        return ResponseUtils.SuccessResponseWithData(mapper.toDTO(customer));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> delete(Long id){
        Customer customer = repo.findById(id).orElseThrow(()-> new CustomException("Customer not found", HttpStatus.NOT_FOUND));

        repo.delete(customer);

        return ResponseUtils.SuccessResponseWithData("Customer deleted", mapper.toDTO(customer));
    }




    //helper methods
    private CustomerResDto buildCustomerResponse(Customer customer) {
        CustomerResDto dto = mapper.toDTO(customer);

        BigDecimal paid = BigDecimal.valueOf(
                Optional.ofNullable(
                        incomeRepo.getTotalTransaction(customer.getId())
                ).orElse(0.0)
        );
        dto.setTotalTransaction(paid);

        List<BigDecimal> perSaleTotals =
                Optional.ofNullable(
                        salesRepo.calculatePerSaleTotalsByCustomer(customer.getId())
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
