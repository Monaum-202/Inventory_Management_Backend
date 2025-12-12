package com.monaum.Rapid_Global.module.incomes.estimate;


import com.monaum.Rapid_Global.module.personnel.user.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

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

        // 1. Calculate Sub Total
        double subTotalAmount = 0.0;
        if (estimate.getItems() != null) {
            subTotalAmount = estimate.getItems().stream()
                    .mapToDouble(EstimateItem::getTotalPrice)
                    .sum();
            dto.setSubTotal(subTotalAmount);
        }

        // 2. Calculate VAT safely (handle null)
        Double vatPercent = dto.getVat();
        double vatAmount = 0.0;
        if (vatPercent != null) {
            vatAmount = subTotalAmount * (vatPercent / 100);
        }

        // 3. Calculate Total safely
        double discount = dto.getDiscount() != null ? dto.getDiscount() : 0.0;
        double totalAmount = subTotalAmount - discount + vatAmount;
        dto.setTotalAmount(totalAmount);
    }
}