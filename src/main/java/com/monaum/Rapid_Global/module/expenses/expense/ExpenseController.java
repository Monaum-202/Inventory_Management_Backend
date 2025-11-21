package com.monaum.Rapid_Global.module.expenses.expense;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 08-Nov-25 11:04 PM
 */

@RestApiController
@RequestMapping("/api/expense")
public class ExpenseController {

    @Autowired ExpenseService service;

    @GetMapping
    public ResponseEntity<BaseApiResponseDTO<?>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("expenseId").descending());

        return service.getAll(search, pageable);
    }

    @PostMapping
    public ResponseEntity<BaseApiResponseDTO<?>> create(
            @Valid @RequestBody ExpenseReqDTO dto
    ){
        return   service.create(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> update(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseReqDTO dto
    ){
      return  service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> delete(
            @PathVariable Long id
    ){
        return  service.delete(id);
    }
}
