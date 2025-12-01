package com.monaum.Rapid_Global.module.incomes.customer;

import com.monaum.Rapid_Global.exception.CustomException;
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

@Service
public class CustomerService {

    @Autowired private CustomerRepo repo;
    @Autowired private CustomerMapper mapper;

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(String search, Pageable pageable){
        Page<CustomerResDto> expenses;
        if (search != null && !search.isBlank()) {
            expenses = repo.search(search, pageable).map(mapper::toDTO);
        }else {
            expenses = repo.findAll(pageable).map(mapper::toDTO);
        }

        CustomPageResponseDTO<CustomerResDto> paginatedResponse = PaginationUtil.buildPageResponse(expenses, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(CustomerReqDto dto){

        Customer entity = mapper.toEntity(dto);
        entity = repo.save(entity);

        return ResponseUtils.SuccessResponseWithData(mapper.toDTO(entity));
    }

    public  ResponseEntity<BaseApiResponseDTO<?>> getById(Long id) throws CustomException {

        Customer customer = repo.findById(id).orElseThrow(()-> new CustomException("Customer not found", HttpStatus.NOT_FOUND));

        return ResponseUtils.SuccessResponseWithData(mapper.toDTO(customer));
    }

    public  ResponseEntity<BaseApiResponseDTO<?>> getByPhone(String phone) throws CustomException {

        Customer customer = repo.findByPhone(phone).orElseThrow(()-> new CustomException("Customer not found", HttpStatus.NOT_FOUND));

        return ResponseUtils.SuccessResponseWithData(mapper.toDTO(customer));
    }


}
