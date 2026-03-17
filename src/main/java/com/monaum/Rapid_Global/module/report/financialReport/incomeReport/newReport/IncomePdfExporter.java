package com.monaum.Rapid_Global.module.report.financialReport.incomeReport.newReport;

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
 * Exports the Income Report as a PDF using JasperReports.
 * Expects: classpath:reports/Income_Report.jrxml
 */
@Component
public class IncomePdfExporter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public void export(IncomeReportDTO report, HttpServletResponse response)
            throws JRException, IOException {

        File jrxml = ResourceUtils.getFile("classpath:reports/Income_Report.jrxml");
        JasperReport compiled = JasperCompileManager.compileReport(jrxml.getAbsolutePath());

        Map<String, Object> params = buildParams(report);
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(report.getRows());
        JasperPrint jasperPrint = JasperFillManager.fillReport(compiled, params, ds);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"Income_Report_" + System.currentTimeMillis() + ".pdf\"");

        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    private Map<String, Object> buildParams(IncomeReportDTO r) {
        Map<String, Object> p = new HashMap<>();
        p.put("dateFrom",      r.getDateFrom()  != null ? r.getDateFrom().format(DATE_FMT)  : "—");
        p.put("dateTo",        r.getDateTo()    != null ? r.getDateTo().format(DATE_FMT)    : "—");
        p.put("statusFilter",  r.getStatusFilter());
        p.put("totalRecords",  r.getTotalRecords());
        p.put("totalAmount",   nvl(r.getTotalAmount()));
        p.put("totalApproved", nvl(r.getTotalApproved()));
        p.put("totalPending",  nvl(r.getTotalPending()));
        return p;
    }

    private Double nvl(BigDecimal val) {
        return val != null ? val.doubleValue() : 0.0;
    }
}