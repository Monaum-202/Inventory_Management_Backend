package com.monaum.Rapid_Global.module.incomes.customer;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestApiController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired private CustomerService service;

    @PostMapping
    public ResponseEntity<BaseApiResponseDTO<?>> create(
            @Valid @RequestBody CustomerReqDto req
    ){
        return service.create(req);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> getById (
            @PathVariable Long id
    ){
        return service.getById(id);
    }

    @GetMapping("/{phone}")
    public ResponseEntity<BaseApiResponseDTO<?>> getByPhone (
            @PathVariable String phone
    ){
        return service.getByPhone(phone);
    }

}
