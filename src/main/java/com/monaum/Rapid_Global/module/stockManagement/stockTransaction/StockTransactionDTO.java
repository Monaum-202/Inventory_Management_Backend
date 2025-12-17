package com.monaum.Rapid_Global.module.stockManagement.stockTransaction;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class StockTransactionDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String transactionType;
    private BigDecimal quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private BigDecimal balanceAfter;
    private LocalDateTime transactionDate;
    private String referenceType;
    private String referenceNumber;
    private String remarks;
}