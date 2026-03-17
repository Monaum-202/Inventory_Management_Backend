package com.monaum.Rapid_Global.module.report.financialReport.cashflowReport;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * ENDPOINTS:
 *   GET /api/reports/cash-flow          → full JSON
 *   GET /api/reports/cash-flow/excel    → .xlsx download
 *   GET /api/reports/cash-flow/pdf      → .pdf download
 */
@RestController
@RequestMapping("/api/reports/cash-flow")
@RequiredArgsConstructor
public class CashFlowReportController {

    private static final int MAX_EXPORT_DAYS = 1830;

    private final CashFlowReportService reportService;
    private final CashFlowExcelExporter excelExporter;
    private final CashFlowPdfExporter   pdfExporter;

    @GetMapping
    public ResponseEntity<CashFlowReportDTO> getReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        validate(dateFrom, dateTo);
        return ResponseEntity.ok(reportService.buildReport(dateFrom, dateTo));
    }

    @GetMapping("/excel")
    public void exportExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            HttpServletResponse response) throws IOException {
        validate(dateFrom, dateTo);
        excelExporter.export(reportService.buildReport(dateFrom, dateTo), response);
    }

    @GetMapping("/pdf")
    public void exportPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            HttpServletResponse response) throws JRException, IOException {
        validate(dateFrom, dateTo);
        pdfExporter.export(reportService.buildReport(dateFrom, dateTo), response);
    }

    private void validate(LocalDate from, LocalDate to) {
        if (from == null || to == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dateFrom and dateTo are required.");
        if (to.isBefore(from))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dateTo must not be before dateFrom.");
        if (from.plusDays(MAX_EXPORT_DAYS).isBefore(to))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date range cannot exceed " + MAX_EXPORT_DAYS + " days.");
    }
}