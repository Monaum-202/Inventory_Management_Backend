package com.monaum.Rapid_Global.module.expenses.purchase;

import com.monaum.Rapid_Global.module.expenses.expense.Expense;
import com.monaum.Rapid_Global.module.expenses.expense.ExpenseMapper;
import com.monaum.Rapid_Global.module.expenses.purchaseItem.PurchaseItem;
import com.monaum.Rapid_Global.module.expenses.purchaseItem.PurchaseItemMapper;
import com.monaum.Rapid_Global.module.personnel.user.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.util.Objects;

@Mapper(componentModel = "spring", uses = { PurchaseItemMapper.class , ExpenseMapper.class } )
public interface PurchaseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "invoiceNo", ignore = true)
    @Mapping(target = "items", source = "items")
    @Mapping(target = "payments", source = "payments")
    Purchase toEntity(PurchaseReqDTO dto);

    @Mapping(target = "paidAmount", ignore = true)
    @Mapping(target = "subTotal", ignore = true)
    @Mapping(target = "dueAmount", ignore = true)
    @Mapping(target = "payments", source = "payments")
    PurchaseResDto toResDto(Purchase entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "invoiceNo", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "items", ignore = true)
    void updateEntityFromDto(PurchaseReqUpdateDTO dto, @MappingTarget Purchase entity);

    default String map(User user) {
        return user != null ? user.getFullName() : null;
    }

    @AfterMapping
    default void computePayments(Purchase sales, @MappingTarget PurchaseResDto dto) {

        // 1. Paid Amount
        BigDecimal paidAmount = BigDecimal.ZERO;
        if (sales.getPayments() != null) {
            paidAmount = sales.getPayments().stream()
                    .map(Expense::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        dto.setPaidAmount(paidAmount);

        // 2. Sub Total
        BigDecimal subTotal = BigDecimal.ZERO;
        if (sales.getItems() != null) {
            subTotal = sales.getItems().stream()
                    .map(PurchaseItem::getTotalPrice)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        dto.setSubTotal(subTotal);

        // 3. VAT
        BigDecimal vatAmount = BigDecimal.ZERO;
        if (dto.getVat() != null) {
            vatAmount = subTotal.multiply(
                    dto.getVat().divide(BigDecimal.valueOf(100))
            );
        }

        // 4. Discount
        BigDecimal discount = dto.getDiscount() != null
                ? dto.getDiscount()
                : BigDecimal.ZERO;

        // 5. Total
        BigDecimal totalAmount = subTotal
                .subtract(discount)
                .add(vatAmount);

        dto.setTotalAmount(totalAmount);

        // 6. Due
        BigDecimal dueAmount = totalAmount.subtract(paidAmount);
        dto.setDueAmount(dueAmount);
    }

}