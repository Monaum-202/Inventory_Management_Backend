package com.monaum.Rapid_Global.module.master.paymentMethod;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

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

    public ResponseEntity<BaseApiResponseDTO<?>> getAll(String search) {
        List<PaymentMethod> transactionCategories;

        if (search != null && !search.isBlank()) {
            transactionCategories = repo.search(search);
        } else {
            transactionCategories = repo.findAll();
        }

        List<ResPaymentMethodDTO> paymentMethodDTOS = transactionCategories.stream().map(mapper::toDTO).toList();

        return ResponseUtils.SuccessResponseWithData("Data fetched successfully.", paymentMethodDTOS);
    }


    public ResponseEntity<BaseApiResponseDTO<?>> getAllActive(Boolean status) {

        List<PaymentMethod> paymentMethods = repo.findAllByActive(status);
        List<ResPaymentMethodDTO> paymentMethodDTOS = paymentMethods.stream().map(mapper::toDTO).toList();

        return ResponseUtils.SuccessResponseWithData("Data fetched successfully.", paymentMethodDTOS);
    }

    public ResponseEntity<BaseApiResponseDTO<?>> getById(Long id) throws CustomException {
        PaymentMethod paymentMethod = repo.findById(id).orElseThrow(() -> new CustomException("Payment Method not found at "+ id , HttpStatus.NOT_FOUND));

        return ResponseUtils.SuccessResponseWithData(mapper.toDTO(paymentMethod));
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> create(ReqPaymentMethodDTO dto){

        PaymentMethod entity = mapper.toEntity(dto);
        entity = repo.save(entity);

        return ResponseUtils.SuccessResponseWithData("Created Successfully!",mapper.toDTO(entity));
    }

    @Transactional
    public ResponseEntity<BaseApiResponseDTO<?>> update(Long id, ReqPaymentMethodDTO dto) throws CustomException {
        PaymentMethod paymentMethod = repo.findById(id).orElseThrow(() -> new CustomException("Payment Method not found", HttpStatus.NOT_FOUND));

        mapper.toEntityUpdate(dto, paymentMethod);
        repo.save(paymentMethod);

        return ResponseUtils.SuccessResponseWithData("Updated Successfully!",mapper.toDTO(paymentMethod));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> activeUpdate(Long id) throws CustomException {
        PaymentMethod paymentMethod = repo.findById(id).orElseThrow(() -> new CustomException("Payment Method not found", HttpStatus.NOT_FOUND));

        paymentMethod.setActive(!Boolean.TRUE.equals(paymentMethod.getActive()));
        repo.save(paymentMethod);

        return ResponseUtils.SuccessResponseWithData("Updated Successfully!",mapper.toDTO(paymentMethod));
    }

    public ResponseEntity<BaseApiResponseDTO<?>> delete(Long id) throws CustomException {
        PaymentMethod paymentMethod = repo.findById(id).orElseThrow(() -> new CustomException("Payment Method not found", HttpStatus.NOT_FOUND));

        repo.delete(paymentMethod);

        return ResponseUtils.SuccessResponse("Payment Method has been deleted successfully", HttpStatus.OK);
    }

}
