package com.monaum.Rapid_Global.module.incomes.customer;

import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.module.master.unit.Unit;
import com.monaum.Rapid_Global.module.master.unit.UnitRepo;
import com.monaum.Rapid_Global.module.master.unit.UnitReqDto;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired private CustomerRepo repo;
    @Autowired private CustomerMapper mapper;

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


}
