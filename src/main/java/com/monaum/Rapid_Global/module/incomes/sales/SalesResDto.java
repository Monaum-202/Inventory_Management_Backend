package com.monaum.Rapid_Global.module.incomes.sales;


import com.monaum.Rapid_Global.module.incomes.income.IncomeResDto;
import com.monaum.Rapid_Global.module.incomes.salesItem.SalesItemResDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SalesResDto {

    private Long id;
    private String invoiceNo;
    private String customerName;
    private String phone;
    private String email;
    private String address;
    private String companyName;
    private LocalDate sellDate;
    private LocalDate deliveryDate;
    private String notes;

    private Double subTotal;
    private Double discount;
    private Double vat;
    private Double totalAmount;
    private Double paidAmount;
    private Double dueAmount;
    private String status;

    private List<SalesItemResDto> items;
    private List<IncomeResDto> payments;
}
