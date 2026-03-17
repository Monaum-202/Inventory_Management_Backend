package com.monaum.Rapid_Global.module.report.financialReport.cashflowReport;

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
 * Expects: classpath:reports/CashFlow_Report.jrxml
 */
@Component
public class CashFlowPdfExporter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public void export(CashFlowReportDTO report, HttpServletResponse response)
            throws JRException, IOException {

        File jrxml = ResourceUtils.getFile("classpath:reports/CashFlow_Report.jrxml");
        JasperReport compiled = JasperCompileManager.compileReport(jrxml.getAbsolutePath());

        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(report.getLineItems());
        JasperPrint print = JasperFillManager.fillReport(compiled, buildParams(report), ds);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"CashFlow_Report_" + System.currentTimeMillis() + ".pdf\"");

        JasperExportManager.exportReportToPdfStream(print, response.getOutputStream());
    }

    private Map<String, Object> buildParams(CashFlowReportDTO r) {
        Map<String, Object> p = new HashMap<>();
        p.put("dateFrom",     r.getDateFrom()  != null ? r.getDateFrom().format(DATE_FMT)  : "—");
        p.put("dateTo",       r.getDateTo()    != null ? r.getDateTo().format(DATE_FMT)    : "—");
        p.put("generatedAt",  r.getGeneratedAt());
        p.put("totalInflow",  nvl(r.getTotalInflow()));
        p.put("totalOutflow", nvl(r.getTotalOutflow()));
        p.put("netCashFlow",  nvl(r.getNetCashFlow().abs()));
        p.put("netLabel",     r.getNetLabel());
        p.put("netFlowPct",   r.getNetFlowPct() != null ? r.getNetFlowPct().doubleValue() : 0.0);
        p.put("isSurplus",    r.getNetCashFlow().compareTo(BigDecimal.ZERO) >= 0);
        return p;
    }

    private Double nvl(BigDecimal v) { return v != null ? v.doubleValue() : 0.0; }
}