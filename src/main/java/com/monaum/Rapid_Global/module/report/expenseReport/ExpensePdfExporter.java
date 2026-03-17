package com.monaum.Rapid_Global.module.report.expenseReport;

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
 * Expects: classpath:reports/Expense_Report.jrxml
 */
@Component
public class ExpensePdfExporter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public void export(ExpenseReportDTO report, HttpServletResponse response)
            throws JRException, IOException {

        File jrxml = ResourceUtils.getFile("classpath:reports/Expense_Report.jrxml");
        JasperReport compiled = JasperCompileManager.compileReport(jrxml.getAbsolutePath());

        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(report.getRows());
        JasperPrint jasperPrint = JasperFillManager.fillReport(compiled, buildParams(report), ds);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"Expense_Report_" + System.currentTimeMillis() + ".pdf\"");

        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }

    private Map<String, Object> buildParams(ExpenseReportDTO r) {
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