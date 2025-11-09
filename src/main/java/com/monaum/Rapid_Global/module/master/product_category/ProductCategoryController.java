package com.monaum.Rapid_Global.module.master.product_category;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestApiController
@RequestMapping("/api/product-category")
public class ProductCategoryController {

    @Autowired private ServiceProductCategory service;

    @PostMapping
    public ResponseEntity<BaseApiResponseDTO<?>> create(
            @Valid @RequestBody ProductCategoryReqDto req
    ){
        return service.create(req);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> getById(
            @PathVariable Long id
    ){
        return  service.getById(id);
    }
}
