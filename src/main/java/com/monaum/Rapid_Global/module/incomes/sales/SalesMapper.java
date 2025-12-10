package com.monaum.Rapid_Global.module.incomes.sales;

import com.monaum.Rapid_Global.module.incomes.income.Income;
import com.monaum.Rapid_Global.module.incomes.income.IncomeMapper;
import com.monaum.Rapid_Global.module.incomes.salesItem.SalesItem;
import com.monaum.Rapid_Global.module.incomes.salesItem.SalesItemMapper;
import com.monaum.Rapid_Global.module.personnel.user.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.util.Objects;

@Mapper(componentModel = "spring", uses = { SalesItemMapper.class , IncomeMapper.class } )
public interface SalesMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "invoiceNo", ignore = true)
    @Mapping(target = "items", source = "items")
    @Mapping(target = "payments", source = "payments")
    Sales toEntity(SalesReqDTO dto);

    @Mapping(target = "paidAmount", ignore = true)
    @Mapping(target = "subTotal", ignore = true)
    @Mapping(target = "dueAmount", ignore = true)
    @Mapping(target = "payments", source = "payments")
    SalesResDto toResDto(Sales entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "invoiceNo", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "items", ignore = true)
    void updateEntityFromDto(SalesReqUpdateDTO dto, @MappingTarget Sales entity);

    default String map(User user) {
        return user != null ? user.getFullName() : null;
    }

    @AfterMapping
    default void computePayments(Sales sales, @MappingTarget SalesResDto dto) {

        // 1. Calculate Paid Amount
        double paidAmount = 0.0;
        if (sales.getPayments() != null) {
            paidAmount = sales.getPayments().stream()
                    .mapToDouble(Income::getAmount)
                    .sum();
            dto.setPaidAmount(paidAmount);
        }

        // 2. Calculate Sub Total
        double subTotalAmount = 0.0;
        if (sales.getItems() != null) {
            subTotalAmount = sales.getItems().stream()
                    .mapToDouble(SalesItem::getTotalPrice)
                    .sum();
            dto.setSubTotal(subTotalAmount);
        }

        // 3. Calculate VAT safely (handle null)
        Double vatPercent = dto.getVat();  // could be null
        double vatAmount = 0.0;
        if (vatPercent != null) {
            vatAmount = subTotalAmount * (vatPercent / 100);
        }

        // Optional: store VAT amount in DTO if you have a field
        // dto.setVatAmount(vatAmount);

        // 4. Calculate Total safely
        double discount = dto.getDiscount() != null ? dto.getDiscount() : 0.0;
        double totalAmount = subTotalAmount - discount + vatAmount;
        dto.setTotalAmount(totalAmount);

        // 5. Calculate Due Amount
        dto.setDueAmount(totalAmount - paidAmount);
    }



}
