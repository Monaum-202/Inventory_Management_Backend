package com.monaum.Rapid_Global.module.incomes.sales;


import com.monaum.Rapid_Global.module.incomes.salesItem.SalesItemResDto;
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
    private String notes;

    private Double totalAmount;
    private Double paidAmount;
    private Double dueAmount;
    private String status;

    private List<SalesItemResDto> items;
}
