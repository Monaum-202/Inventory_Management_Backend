package com.monaum.Rapid_Global.module.stockManagement.stock;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.module.production.itemUsage.ItemUsageDTO;
import com.monaum.Rapid_Global.module.production.production.StockOutService;
import com.monaum.Rapid_Global.module.stockManagement.stockTransaction.StockTransactionService;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 21-Dec-25 10:40 PM
 */

@RestApiController
@RequestMapping("/api/stock")
@AllArgsConstructor
public class StockController {

    private StockService service;
    private StockOutService stockService;
    private StockTransactionService stockTransactionService;


    @GetMapping
    public ResponseEntity<BaseApiResponseDTO<?>> getAll(){
       return service.getAll();
    }

    @GetMapping("/transection")
    public ResponseEntity<BaseApiResponseDTO<?>> getAllTransection(Pageable pageable){
        return stockTransactionService.getAll(pageable);
    }

    @PostMapping
    public ResponseEntity<BaseApiResponseDTO<?>> addStock(@RequestBody ItemUsageDTO dto) {
        return stockService.stockOut(dto);
    }
}
