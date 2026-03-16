package com.monaum.Rapid_Global.module.report.purchaseReport;

import com.monaum.Rapid_Global.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * One row in the Purchase Report table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseReportRowDTO {

    private Long        id;
    private String      invoiceNo;
    private String      supplierName;
    private String      phone;
    private LocalDate   purchaseDate;
    private LocalDate   deliveryDate;
    private int         itemCount;

    private BigDecimal  subTotal;
    private BigDecimal  discount;
    private BigDecimal  vat;
    private BigDecimal  totalAmount;
    private BigDecimal  paidAmount;
    private BigDecimal  dueAmount;

    private OrderStatus status;
}