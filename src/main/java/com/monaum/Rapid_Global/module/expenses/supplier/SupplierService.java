package com.monaum.Rapid_Global.module.expenses.supplier;

import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.module.master.unit.Unit;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SupplierService {
    @Autowired private SupplierRepo repo;
    @Autowired private  SupplierMapper mapper;

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
}
