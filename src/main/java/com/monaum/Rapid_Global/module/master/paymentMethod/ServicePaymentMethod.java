package com.monaum.Rapid_Global.module.master.paymentMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 01-NOV-25 9:49 PM
 */

@Service
@RequiredArgsConstructor
public class ServicePaymentMethod {

    @Autowired private RepoPaymentMethod repo;
    @Autowired private MapperPaymentMethod mapper;

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(ReqPaymentMethodDTO dto){

        PaymentMethod entity = mapper.toEntity(dto);
        entity = repo.save(entity);

        return ResponseUtils.SuccessResponseWithData(mapper.toDTO(entity));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(Pageable pageable){

    }

}
