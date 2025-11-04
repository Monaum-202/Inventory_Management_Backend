package com.monaum.Rapid_Global.module.master.unit;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.module.master.product_category.ProductCategoryReqDto;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestApiController
@RequestMapping("/api/unit")
public class UnitController {

    @Autowired private UnitService service;

    @PostMapping
    public ResponseEntity<BaseApiResponseDTO<?>> create(
            @Valid @RequestBody UnitReqDto req
    ){
        return service.create(req);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> getById(
            @PathVariable Long id
    ){
        return service.getById(id);
    }


}
