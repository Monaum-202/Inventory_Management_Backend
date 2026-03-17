package com.monaum.Rapid_Global.module.report.financialReport.profitLoss;

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
 *   GET /api/reports/profit-loss            → full JSON report
 *   GET /api/reports/profit-loss/excel      → .xlsx download
 *   GET /api/reports/profit-loss/pdf        → .pdf download
 *
 * Date range is MANDATORY for all three endpoints —
 * a P&L without a defined period is meaningless.
 */
@RestController
@RequestMapping("/api/reports/profit-loss")
@RequiredArgsConstructor
public class ProfitLossReportController {

    private static final int MAX_EXPORT_DAYS = 1830; // 5 years

    private final ProfitLossReportService reportService;
    private final ProfitLossExcelExporter excelExporter;
    private final ProfitLossPdfExporter   pdfExporter;

    // ----------------------------------------------------------------
    // 1. JSON
    // ----------------------------------------------------------------

    @GetMapping
    public ResponseEntity<ProfitLossReportDTO> getReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo
    ) {
        validateDateRange(dateFrom, dateTo);
        return ResponseEntity.ok(reportService.buildReport(dateFrom, dateTo));
    }

    // ----------------------------------------------------------------
    // 2. Excel
    // ----------------------------------------------------------------

    @GetMapping("/excel")
    public void exportExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            HttpServletResponse response
    ) throws IOException {
        validateDateRange(dateFrom, dateTo);
        excelExporter.export(reportService.buildReport(dateFrom, dateTo), response);
    }

    // ----------------------------------------------------------------
    // 3. PDF
    // ----------------------------------------------------------------

    @GetMapping("/pdf")
    public void exportPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            HttpServletResponse response
    ) throws JRException, IOException {
        validateDateRange(dateFrom, dateTo);
        pdfExporter.export(reportService.buildReport(dateFrom, dateTo), response);
    }

    // ================================================================
    // HELPERS
    // ================================================================

    private void validateDateRange(LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom == null || dateTo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "dateFrom and dateTo are required.");
        }
        if (dateTo.isBefore(dateFrom)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "dateTo must not be before dateFrom.");
        }
        if (dateFrom.plusDays(MAX_EXPORT_DAYS).isBefore(dateTo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Date range cannot exceed " + MAX_EXPORT_DAYS + " days.");
        }
    }
}