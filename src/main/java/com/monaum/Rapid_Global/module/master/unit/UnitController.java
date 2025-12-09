package com.monaum.Rapid_Global.module.master.unit;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.enums.TransactionType;
import com.monaum.Rapid_Global.module.master.product_category.ProductCategoryReqDto;
import com.monaum.Rapid_Global.module.master.transectionCategory.TransactionCategoryReqDto;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestApiController
@RequestMapping("/api/unit")
public class UnitController {

    @Autowired private UnitService service;

    @GetMapping
    public ResponseEntity<BaseApiResponseDTO<?>> getAll(
            @RequestParam(required = false) String search
    ){
        return service.getAll(search);
    }

//    @GetMapping("/all-active")
//    public ResponseEntity<BaseApiResponseDTO<?>> getAllActive(
//            @RequestParam Boolean status,
//            @RequestParam TransactionType type
//    ){
//        return service.getAllActive(status, type);
//    }

    @GetMapping("/all-active")
    public ResponseEntity<BaseApiResponseDTO<?>> getAllActive(
            @RequestParam Boolean status
    ){
        return service.getAllActive(status);
    }

    @PostMapping
    public ResponseEntity<BaseApiResponseDTO<?>> create(
            @Valid @RequestBody UnitReqDto req
    ){
        return service.create(req);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> update(
            @PathVariable Long id,
            @Valid @RequestBody UnitReqDto req
    ){
        return service.update(id, req);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> getById(
            @PathVariable Long id
    ){
        return service.getById(id);
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
