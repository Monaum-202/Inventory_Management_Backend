package com.monaum.Rapid_Global.module.master.paymentMethod;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import com.monaum.Rapid_Global.util.response.CustomPageResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * Created on 11/2/2025 at 10:28 AM
 */

@RestApiController
@RequestMapping("/api/payment-method")
public class PaymentMethodController {

    @Autowired private ServicePaymentMethod service;

    @GetMapping
    public ResponseEntity<BaseApiResponseDTO<?>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("sqn").ascending());

        return service.getAll(pageable);
    }

    @GetMapping("/all-active")
    public ResponseEntity<BaseApiResponseDTO<?>> getAllActive(
            @RequestParam Boolean status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by("sqn").ascending());

        return service.getAllActive(status,pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> getById(
            @PathVariable Long id
    ){
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<BaseApiResponseDTO<?>> create(
            @Valid @RequestBody ReqPaymentMethodDTO req
    ){
        return service.create(req);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> update(
            @PathVariable Long id,
            @Valid @RequestBody ReqPaymentMethodDTO req
    ){
        return service.update(id, req);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BaseApiResponseDTO<?>> updateStatus(
            @PathVariable Long id
    ){
        return service.updateStatus(id);
    }

}
