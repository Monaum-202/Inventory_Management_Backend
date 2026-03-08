package com.monaum.Rapid_Global.module.report.salesReport.newReport;

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
 * One row in the Sales Report table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesReportRowDTO {

    private Long id;
    private String invoiceNo;
    private String customerName;
    private String phone;
    private LocalDate sellDate;
    private LocalDate deliveryDate;
    private int itemCount;

    private BigDecimal subTotal;
    private BigDecimal discount;
    private BigDecimal vat;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal dueAmount;

    private OrderStatus status;
}