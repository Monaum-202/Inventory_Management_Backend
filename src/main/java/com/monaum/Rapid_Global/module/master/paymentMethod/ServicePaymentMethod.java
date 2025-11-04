package com.monaum.Rapid_Global.module.master.paymentMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.util.PaginationUtil;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import com.monaum.Rapid_Global.util.response.CustomPageResponseDTO;

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

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(Pageable pageable){

        Page<ResPaymentMethodDTO> page = repo.findAll(pageable).map(mapper::toDTO);
        CustomPageResponseDTO<ResPaymentMethodDTO> paginatedResponse = PaginationUtil.buildPageResponse(page, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getAllActive(Boolean status, Pageable pageable){

        Page<ResPaymentMethodDTO> page = repo.findAllByActive(status,pageable).map(mapper::toDTO);
        CustomPageResponseDTO<ResPaymentMethodDTO> paginatedResponse = PaginationUtil.buildPageResponse(page, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getById(Long id) throws CustomException {
        PaymentMethod paymentMethod = repo.findById(id).orElseThrow(() -> new CustomException("Payment Method not found at "+ id , HttpStatus.NOT_FOUND));

        return ResponseUtils.SuccessResponseWithData(mapper.toDTO(paymentMethod));
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(ReqPaymentMethodDTO dto){

        PaymentMethod entity = mapper.toEntity(dto);
        entity = repo.save(entity);

        return ResponseUtils.SuccessResponseWithData(mapper.toDTO(entity));
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> update(Long id, ReqPaymentMethodDTO dto) throws CustomException {
        PaymentMethod paymentMethod = repo.findById(id).orElseThrow(() -> new CustomException("Payment Method not found", HttpStatus.NOT_FOUND));

        mapper.toEntityUpdate(dto, paymentMethod);
        repo.save(paymentMethod);

        return ResponseUtils.SuccessResponseWithData(mapper.toDTO(paymentMethod));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> updateStatus(Long id) throws CustomException {
        PaymentMethod paymentMethod = repo.findById(id).orElseThrow(() -> new CustomException("Payment Method not found", HttpStatus.NOT_FOUND));

        paymentMethod.setActive(!Boolean.TRUE.equals(paymentMethod.getActive()));
        repo.save(paymentMethod);

        return ResponseUtils.SuccessResponseWithData(mapper.toDTO(paymentMethod));
    }

}
