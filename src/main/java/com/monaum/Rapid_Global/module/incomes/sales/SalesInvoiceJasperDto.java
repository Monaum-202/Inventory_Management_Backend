package com.monaum.Rapid_Global.module.incomes.sales;

import com.monaum.Rapid_Global.module.incomes.salesItem.SalesItemResDto;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesInvoiceJasperDto {
    private String invoiceNo;
    private String customerName;
    private String phone;
    private String address;
    private String sellDate;
    private String deliveryDate;

    private BigDecimal subTotal;
    private BigDecimal discount;
    private BigDecimal vat;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal dueAmount;
    private String amountInWords;

    private List<SalesInvoiceItemDto> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesInvoiceItemDto {
        private String itemName;
        private String unitName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
}
