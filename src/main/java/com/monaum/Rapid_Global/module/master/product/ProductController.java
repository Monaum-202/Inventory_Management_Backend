package com.monaum.Rapid_Global.module.master.product;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.enums.ProductType;
import com.monaum.Rapid_Global.module.master.unit.UnitReqDto;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 09-Dec-25 9:50 PM
 */

@RestApiController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired private ProductService service;

    @GetMapping
    public ResponseEntity<BaseApiResponseDTO<?>> getAll(
            @RequestParam(required = false) String search
    ){
        return service.getAll(search);
    }

//    @GetMapping("/all-active")
//    public ResponseEntity<BaseApiResponseDTO<?>> getAllActive(
//            @RequestParam Boolean status
//    ){
//        return service.getAllActive(status);
//    }

    @GetMapping("/all-active")
    public ResponseEntity<BaseApiResponseDTO<?>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false) ProductType type
            ) {
        return service.getAll(search, status, type);
    }

    @PostMapping
    public ResponseEntity<BaseApiResponseDTO<?>> create(
            @Valid @RequestBody ProductReqDto dto
    ){
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductReqDto req
    ){
        return service.update(id, req);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> activeUpdate(
            @PathVariable Long id
    ){
        return service.activeUpdate(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> delete(
            @PathVariable Long id
    ){
        return service.delete(id);
    }
}
