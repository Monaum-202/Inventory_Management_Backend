//package com.monaum.Rapid_Global.module.production.production;
//
//import com.monaum.Rapid_Global.annotations.RestApiController;
//import com.monaum.Rapid_Global.module.production.itemUsage.ItemUsageDTO;
//import com.monaum.Rapid_Global.module.stockManagement.stock.StockDTO;
//import com.monaum.Rapid_Global.module.stockManagement.stock.StockService;
//import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//
///**
// * Monaum Hossain
// * monaum.202@gmail.com
// * @since 26-Dec-25 12:04 AM
// */
//
//@RestApiController
//@RequestMapping("/api/Stock")
//public class StockController {
//
//    @Autowired private StockOutService stockService;
//
//    @PostMapping
//    public ResponseEntity<BaseApiResponseDTO<?>> addStock(@RequestBody ItemUsageDTO dto) {
//        return stockService.stockOut(dto);
//    }
//}
