package com.monaum.Rapid_Global.module.expenses.purchase;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.module.incomes.sales.SalesReqDTO;
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
 *
 * @since 17-Dec-25 12:35 AM
 */

@RestApiController
@RequestMapping("/api/purchase")
public class PurchaseController {

    @Autowired private PurchaseService service;

    @GetMapping
    public ResponseEntity<BaseApiResponseDTO<?>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("invoiceNo").descending());
        return service.getAll(search, pageable);
    }

    @PostMapping
    public ResponseEntity<BaseApiResponseDTO<?>> create(
            @Valid @RequestBody PurchaseReqDTO dto
    ){
        return   service.create(dto);
    }
}
