package com.monaum.Rapid_Global.module.incomes.customer;
import com.monaum.Rapid_Global.module.incomes.income.Income;
import com.monaum.Rapid_Global.module.incomes.sales.Sales;
import com.monaum.Rapid_Global.module.incomes.sales.SalesResDto;
import com.monaum.Rapid_Global.module.master.unit.Unit;
import com.monaum.Rapid_Global.module.master.unit.UnitResDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

//    @Mapping(target = "totalTransaction", ignore = true)
    Customer toEntity(CustomerReqDto dto);

//    @Mapping(target = "payments", source = "payments")
    CustomerResDto toDTO(Customer entity);
    void toEntity(CustomerReqDto dto, @MappingTarget Customer customer);

//    @AfterMapping
//    default void computePayments(Customer sales, @MappingTarget CustomerResDto dto) {
//
//        // 1. Calculate Paid Amount
//        double paidAmount = 0.0;
//        if (sales.getPayments() != null) {
//            paidAmount = sales.getPayments().stream()
//                    .mapToDouble(Income::getAmount)
//                    .sum();
//            dto.setPaidAmount(paidAmount);
//        }
//    }

}
