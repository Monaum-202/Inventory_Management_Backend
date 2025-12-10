package com.monaum.Rapid_Global.module.incomes.sales;

import com.monaum.Rapid_Global.module.incomes.salesItem.SalesItemResDto;
import lombok.*;

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

    private Double subTotal;
    private Double discount;
    private Double vat;
    private Double totalAmount;
    private Double paidAmount;
    private Double dueAmount;
    private String amountInWords;

    private List<SalesInvoiceItemDto> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesInvoiceItemDto {
        private String itemName;
        private String unitName;
        private Integer quantity;
        private Double unitPrice;
        private Double totalPrice;
    }
}
