package com.monaum.Rapid_Global.module.incomes.income;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.enums.Status;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestApiController
@RequestMapping("/api/income")
public class IncomeController {

    @Autowired private IncomeService service;

    @GetMapping
    public ResponseEntity<BaseApiResponseDTO<?>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("incomeId").descending());

        return service.getAll(search, pageable);
    }

    @PostMapping
    public ResponseEntity<BaseApiResponseDTO<?>> create(
            @Valid @RequestBody IncomeReqDTO dto
    ){
        return   service.create(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> update(
            @PathVariable Long id,
            @Valid @RequestBody IncomeReqDTO dto
    ){
        return  service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> delete(
            @PathVariable Long id
    ){
        return  service.delete(id);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<BaseApiResponseDTO<?>> approve(@PathVariable Long id) {
        return service.updateStatus(id, Status.APPROVED,null);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BaseApiResponseDTO<?>> cancel(
            @PathVariable Long id,
            @RequestBody String reason
    ) {
        return service.updateStatus(id, Status.CANCELED, reason);
    }
}
