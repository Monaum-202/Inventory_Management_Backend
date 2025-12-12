package com.monaum.Rapid_Global.module.incomes.estimate;

import com.monaum.Rapid_Global.enums.OrderStatus;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 12-Dec-25
 */

@RestController
@RequestMapping("/api/estimates")
@RequiredArgsConstructor
public class EstimateController {

    private final EstimateService estimateService;

    @PostMapping
    public ResponseEntity<BaseApiResponseDTO<?>> create(@Valid @RequestBody EstimateReqDTO dto) {
        return estimateService.create(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> update(
            @PathVariable Long id,
            @Valid @RequestBody EstimateReqDTO dto) {
        return estimateService.update(id, dto);
    }

    @GetMapping("/{id}")
    public EstimateResDto getById(@PathVariable Long id) {
        return estimateService.getById(id);
    }

    @GetMapping
    public ResponseEntity<BaseApiResponseDTO<?>> getAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10) Pageable pageable) {
        return estimateService.getAll(search, pageable);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        estimateService.delete(id);
    }

    @PostMapping("/{id}/convert-to-sale")
    public ResponseEntity<BaseApiResponseDTO<?>> convertToSale(
            @PathVariable Long id,
            @RequestBody ConvertToSaleDTO dto) {
        return estimateService.convertToSale(id, dto);
    }
}