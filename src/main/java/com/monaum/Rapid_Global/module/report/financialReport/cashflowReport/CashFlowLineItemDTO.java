package com.monaum.Rapid_Global.module.report.financialReport.cashflowReport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * lineType is a plain String to avoid JasperReports inner-type binary name errors.
 * Valid values: SECTION_HEADER | CATEGORY | SUBTOTAL | SPACER | NET
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashFlowLineItemDTO {

    /** SECTION_HEADER | CATEGORY | SUBTOTAL | SPACER | NET */
    private String     lineType;
    private String     label;
    private BigDecimal amount;
    private BigDecimal percentage;
    /** true = inflow (green), false = outflow (red), null = neutral */
    private Boolean    positive;
}