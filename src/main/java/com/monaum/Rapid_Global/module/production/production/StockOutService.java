package com.monaum.Rapid_Global.module.production.production;

import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.module.master.product.Product;
import com.monaum.Rapid_Global.module.master.product.ProductRepo;
import com.monaum.Rapid_Global.module.production.item.ItemDTO;
import com.monaum.Rapid_Global.module.production.itemUsage.ItemUsage;
import com.monaum.Rapid_Global.module.production.itemUsage.ItemUsageDTO;
import com.monaum.Rapid_Global.module.production.itemUsage.ItemUsageMapper;
import com.monaum.Rapid_Global.module.stockManagement.stock.StockService;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 25-Dec-25 1:20 AM
 */

@Service
@Slf4j
@AllArgsConstructor
public class StockOutService {

    private final ProductRepo productRepo;
    private final StockService stockService;
    private final ItemUsageMapper mapper;

    public ResponseEntity<BaseApiResponseDTO<?>> stockOut(ItemUsageDTO dto) {
        for (ItemDTO item : dto.getItems()) {

            Product product = productRepo.findById(item.getProductId()).orElseThrow(() -> new CustomException("Product Not Found", HttpStatus.NOT_FOUND));

            stockService.removeStock(product, item.getQuantity(), null, null,"USAGE: " );
        }

        ItemUsage itemUsage = mapper.toEntity(dto);

        return ResponseUtils.SuccessResponseWithData("Sales created successfully!", mapper.toResponse(itemUsage));
    }
}
