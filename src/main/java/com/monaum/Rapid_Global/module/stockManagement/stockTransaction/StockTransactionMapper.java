package com.monaum.Rapid_Global.module.stockManagement.stockTransaction;

import com.monaum.Rapid_Global.module.stockManagement.stockTransaction.StockTransaction;
import org.mapstruct.*;
import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface StockTransactionMapper {

    @Mappings({
            @Mapping(source = "product.id", target = "productId"),
            @Mapping(source = "product.name", target = "productName"),
            @Mapping(source = "type", target = "transactionType"),
            @Mapping(source = "balance", target = "balanceAfter"),
            @Mapping(source = "createdAt", target = "transactionDate"),
            @Mapping(source = "referenceId", target = "referenceNumber"),
            @Mapping(target = "createdBy", source = "createdBy.fullName")
    })
    StockTransactionResDTO toDto(StockTransaction entity);
}