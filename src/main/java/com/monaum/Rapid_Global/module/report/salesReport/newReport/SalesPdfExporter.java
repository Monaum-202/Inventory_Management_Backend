package com.monaum.Rapid_Global.module.report.salesReport.newReport;

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
 * Exports the Sales List Report as a PDF using JasperReports.
 * Expects:  classpath:reports/Sales_Report.jrxml
 */
@Component
public class SalesPdfExporter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public void export(SalesReportDTO report, HttpServletResponse response)
            throws JRException, IOException {

        // ---- load & compile the JRXML ----
        File jrxml = ResourceUtils.getFile("classpath:reports/Sales_Report.jrxml");
        JasperReport compiled = JasperCompileManager.compileReport(jrxml.getAbsolutePath());

        // ---- parameters (summary section) ----
        Map<String, Object> params = buildParams(report);

        // ---- data source (detail rows) ----
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(report.getRows());

        // ---- fill ----
        JasperPrint jasperPrint = JasperFillManager.fillReport(compiled, params, ds);

        // ---- stream to response ----
        response.setContentType("application/pdf");
        String filename = "Sales_Report_" + System.currentTimeMillis() + ".pdf";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    // ----------------------------------------------------------------

    private Map<String, Object> buildParams(SalesReportDTO r) {
        Map<String, Object> p = new HashMap<>();

        p.put("dateFrom",     r.getDateFrom()  != null ? r.getDateFrom().format(DATE_FMT)  : "—");
        p.put("dateTo",       r.getDateTo()    != null ? r.getDateTo().format(DATE_FMT)    : "—");
        p.put("statusFilter", r.getStatusFilter());
        p.put("totalOrders",  r.getTotalOrders());

        p.put("totalSubAmount", nvl(r.getTotalSubAmount()));
        p.put("totalDiscount",  nvl(r.getTotalDiscount()));
        p.put("totalVat",       nvl(r.getTotalVat()));
        p.put("totalAmount",    nvl(r.getTotalAmount()));
        p.put("totalPaid",      nvl(r.getTotalPaid()));
        p.put("totalDue",       nvl(r.getTotalDue()));

        return p;
    }

    private Double nvl(BigDecimal val) {
        return val != null ? val.doubleValue() : 0.0;
    }
}