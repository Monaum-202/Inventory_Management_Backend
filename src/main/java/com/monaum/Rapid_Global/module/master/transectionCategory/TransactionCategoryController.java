package com.monaum.Rapid_Global.module.master.transectionCategory;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.enums.TransactionType;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
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
 * @since 14-Nov-25 10:27 AM
 */

@RestApiController
@RequestMapping("/api/transection-category")
public class TransactionCategoryController {

    @Autowired private TransactionCategoryService service;

    @GetMapping
    public ResponseEntity<BaseApiResponseDTO<?>> getAll(
            @RequestParam(required = false) String search
    ){
        return service.getAll(search);
    }

    @GetMapping("/all-active")
    public ResponseEntity<BaseApiResponseDTO<?>> getAllActive(
            @RequestParam Boolean status,
            @RequestParam TransactionType type
    ){
        return service.getAllActive(status, type);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> getById(
            @PathVariable Long id
    ){
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<BaseApiResponseDTO<?>> create(
            @Valid @RequestBody TransactionCategoryReqDto req
    ){
        return service.create(req);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> update(
            @PathVariable Long id,
            @Valid @RequestBody TransactionCategoryReqDto req
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
