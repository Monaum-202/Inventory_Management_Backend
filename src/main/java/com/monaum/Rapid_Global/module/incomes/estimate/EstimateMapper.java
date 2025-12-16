package com.monaum.Rapid_Global.module.incomes.estimate;


import com.monaum.Rapid_Global.module.personnel.user.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 12-Dec-25
 */

@Mapper(componentModel = "spring", uses = { EstimateItemMapper.class })
public interface EstimateMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estimateNo", ignore = true)
    @Mapping(target = "items", source = "items")
    @Mapping(target = "convertedToSale", ignore = true)
    @Mapping(target = "saleId", ignore = true)
    Estimate toEntity(EstimateReqDTO dto);

    @Mapping(target = "subTotal", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "items", source = "items")
    EstimateResDto toResDto(Estimate entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estimateNo", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "convertedToSale", ignore = true)
    @Mapping(target = "saleId", ignore = true)
    void updateEntityFromDto(EstimateReqDTO dto, @MappingTarget Estimate entity);

    default String map(User user) {
        return user != null ? user.getFullName() : null;
    }

    @AfterMapping
    default void computeTotals(Estimate estimate, @MappingTarget EstimateResDto dto) {

        // 1. Sub Total
        BigDecimal subTotal = BigDecimal.ZERO;

        if (estimate.getItems() != null) {
            subTotal = estimate.getItems().stream()
                    .map(EstimateItem::getTotalPrice) // BigDecimal
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        dto.setSubTotal(subTotal.doubleValue()); // or change DTO to BigDecimal

        // 2. VAT
        BigDecimal vatAmount = BigDecimal.ZERO;
        if (dto.getVat() != null) {
            vatAmount = subTotal.multiply(
                    BigDecimal.valueOf(dto.getVat()).divide(BigDecimal.valueOf(100))
            );
        }

        // 3. Discount
        BigDecimal discount = dto.getDiscount() != null
                ? BigDecimal.valueOf(dto.getDiscount())
                : BigDecimal.ZERO;

        // 4. Total
        BigDecimal totalAmount = subTotal.subtract(discount).add(vatAmount);

        dto.setTotalAmount(totalAmount.doubleValue());
    }

}