package com.monaum.Rapid_Global.module.stockManagement.stock;

import org.mapstruct.Mapper;
import com.monaum.Rapid_Global.module.master.product.Product;
import org.mapstruct.*;
import java.math.BigDecimal;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 21-Dec-25 10:58 PM
 */

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StockMapper {

    @Mapping(source = "stock.id", target = "id")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.unit.name", target = "unitName")
    @Mapping(source = "stock.quantity", target = "currentQuantity")
    @Mapping(source = "stock.alertQuantity", target = "minimumStockLevel")
    @Mapping(source = "stock.averageCost", target = "averageCost")
    @Mapping(target = "reservedQuantity", expression = "java(reservedQuantity)")
    @Mapping(target = "availableQuantity",
            expression = "java(calculateAvailable(stock.getQuantity(), reservedQuantity))")
    @Mapping(target = "stockStatus",
            expression = "java(determineStockStatus(stock.getQuantity(), stock.getAlertQuantity()))")
    @Mapping(target = "stockValue",
            expression = "java(stock.getQuantity().multiply(stock.getAverageCost()))")
    StockDTO toDto(
            Stock stock,
            Product product,
            BigDecimal reservedQuantity
    );

    /**
     * Default mapper for simple usage
     * Uses ZERO reserved quantity
     */
    default StockDTO toDto(Stock stock) {
        return toDto(stock, stock.getProduct(), BigDecimal.ZERO);
    }

    default BigDecimal calculateAvailable(BigDecimal current, BigDecimal reserved) {
        if (current == null) return BigDecimal.ZERO;
        if (reserved == null) return BigDecimal.ZERO;
        return current.subtract(reserved);
    }

    default String determineStockStatus(BigDecimal quantity, BigDecimal alertQuantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            return "LOW";
        }
        if (alertQuantity == null) {
            return "ADEQUATE";
        }
        if (quantity.compareTo(alertQuantity) <= 0) {
            return "LOW";
        }
        if (quantity.compareTo(alertQuantity.multiply(BigDecimal.valueOf(3))) >= 0) {
            return "OVERSTOCK";
        }
        return "ADEQUATE";
    }
}