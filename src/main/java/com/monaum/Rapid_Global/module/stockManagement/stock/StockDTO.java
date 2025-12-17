package com.monaum.Rapid_Global.module.stockManagement.stock;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class StockDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String unitName;
    private BigDecimal currentQuantity;
    private BigDecimal reservedQuantity;
    private BigDecimal availableQuantity;
    private BigDecimal minimumStockLevel;
    private BigDecimal averageCost;
    private BigDecimal lastPurchasePrice;
    private LocalDateTime lastPurchaseDate;
    private String stockStatus; // LOW, ADEQUATE, OVERSTOCK
    private BigDecimal stockValue;
}