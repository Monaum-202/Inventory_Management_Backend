package com.monaum.Rapid_Global.module.incomes.sales;

import com.monaum.Rapid_Global.module.incomes.income.Income;
import com.monaum.Rapid_Global.module.incomes.income.IncomeMapper;
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
    Sales toEntity(SalesReqDTO dto);

    @Mapping(target = "paidAmount", ignore = true)
    @Mapping(target = "dueAmount", ignore = true)
    @Mapping(target = "payments", source = "payments")
    SalesResDto toResDto(Sales entity);

    default String map(User user) {
        return user != null ? user.getFullName() : null;
    }

    @AfterMapping
    default void computePaidAmount(Sales sales, @MappingTarget SalesResDto dto) {

        if (sales.getPayments() != null) {
            double paidAmount = sales.getPayments().stream()
                    .mapToDouble(Income::getAmount)
                    .sum();

            dto.setPaidAmount(paidAmount);
            dto.setDueAmount(dto.getTotalAmount() - paidAmount);
        }
    }

}
