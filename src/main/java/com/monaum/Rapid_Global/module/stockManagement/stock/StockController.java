package com.monaum.Rapid_Global.module.stockManagement.stock;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 21-Dec-25 10:40 PM
 */

@RestApiController
@RequestMapping("/api/stock")
public class StockController {

    @Autowired StockService service;

    @GetMapping
    public ResponseEntity<BaseApiResponseDTO<?>> getAll(){
       return service.getAll();
    }
}
