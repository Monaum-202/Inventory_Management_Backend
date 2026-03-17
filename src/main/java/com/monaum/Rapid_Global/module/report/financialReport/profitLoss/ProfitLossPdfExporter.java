package com.monaum.Rapid_Global.module.report.financialReport.profitLoss;

import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * Expects: classpath:reports/ProfitLoss_Report.jrxml
 */
@Component
public class ProfitLossPdfExporter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public void export(ProfitLossReportDTO report, HttpServletResponse response)
            throws JRException, IOException {

        File jrxml = ResourceUtils.getFile("classpath:reports/ProfitLoss_Report.jrxml");
        JasperReport compiled = JasperCompileManager.compileReport(jrxml.getAbsolutePath());

        // Data source is the flat ordered list of line items
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(report.getLineItems());
        JasperPrint jasperPrint = JasperFillManager.fillReport(compiled, buildParams(report), ds);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"PL_Report_" + System.currentTimeMillis() + ".pdf\"");

        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    private Map<String, Object> buildParams(ProfitLossReportDTO r) {
        Map<String, Object> p = new HashMap<>();
        p.put("dateFrom",     r.getDateFrom()  != null ? r.getDateFrom().format(DATE_FMT)  : "—");
        p.put("dateTo",       r.getDateTo()    != null ? r.getDateTo().format(DATE_FMT)    : "—");
        p.put("generatedAt",  r.getGeneratedAt());
        p.put("totalIncome",  nvl(r.getTotalIncome()));
        p.put("totalExpense", nvl(r.getTotalExpense()));
        p.put("netProfit",    nvl(r.getNetProfit().abs())); // always positive; sign from netLabel
        p.put("netLabel",     r.getNetLabel());
        p.put("netMarginPct", r.getNetMarginPct() != null ? r.getNetMarginPct().doubleValue() : 0.0);
        p.put("isProfit",     r.getNetProfit().compareTo(BigDecimal.ZERO) >= 0);
        return p;
    }

    private Double nvl(BigDecimal v) {
        return v != null ? v.doubleValue() : 0.0;
    }
}