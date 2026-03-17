package com.monaum.Rapid_Global.module.report.financialReport.profitLoss;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * lineType is a plain String (not an inner enum) so JasperReports'
 * expression compiler can reference it without the inner-type binary
 * name problem (OuterClass$InnerEnum is not resolvable in JRXML).
 *
 * Valid lineType values: SECTION_HEADER | CATEGORY | SUBTOTAL | SPACER | NET
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfitLossLineItemDTO {

    /** One of: SECTION_HEADER, CATEGORY, SUBTOTAL, SPACER, NET */
    private String     lineType;

    private String     label;
    /** null for SPACER and SECTION_HEADER rows */
    private BigDecimal amount;
    /** percentage share within the section — null except for CATEGORY rows */
    private BigDecimal percentage;
    /** true = green colour cue; false = red; null = neutral */
    private Boolean    positive;
}