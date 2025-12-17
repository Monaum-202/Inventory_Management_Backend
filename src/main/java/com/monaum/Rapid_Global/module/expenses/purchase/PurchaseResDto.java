package com.monaum.Rapid_Global.module.expenses.purchase;

import com.monaum.Rapid_Global.module.expenses.expense.ExpenseResDto;
import com.monaum.Rapid_Global.module.expenses.purchaseItem.PurchaseItemResDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PurchaseResDto {

    private Long id;
    private String invoiceNo;
    private String supplierName;
    private String phone;
    private String email;
    private String address;
    private String companyName;
    private LocalDate purchaseDate;
    private LocalDate deliveryDate;
    private String notes;

    private BigDecimal subTotal;
    private BigDecimal discount;
    private BigDecimal vat;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal dueAmount;
    private String status;

    private List<PurchaseItemResDto> items;
    private List<ExpenseResDto> payments;
}