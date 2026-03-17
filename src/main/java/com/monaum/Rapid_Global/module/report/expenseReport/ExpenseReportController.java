package com.monaum.Rapid_Global.module.report.expenseReport;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
 *   GET /api/reports/expenses               → paginated JSON
 *   GET /api/reports/expenses/summary       → totals only (no rows)
 *   GET /api/reports/expenses/excel         → .xlsx download (streaming)
 *   GET /api/reports/expenses/pdf           → .pdf download (max PDF_ROW_LIMIT rows)
 */
@RestController
@RequestMapping("/api/reports/expenses")
@RequiredArgsConstructor
public class ExpenseReportController {

    private static final int PDF_ROW_LIMIT   = 5_000;
    private static final int MAX_EXPORT_DAYS = 366;

    private final ExpenseReportService  reportService;
    private final ExpenseExcelExporter  excelExporter;
    private final ExpensePdfExporter    pdfExporter;

    // ----------------------------------------------------------------
    // 1. Paginated JSON
    // ----------------------------------------------------------------

    @GetMapping
    public ResponseEntity<Page<ExpenseReportRowDTO>> getReport(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,

            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paidTo,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String employeeName,

            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "50")  int size
    ) {
        size = Math.min(size, 200);
        Pageable pageable = PageRequest.of(page, size, Sort.by("expenseDate").descending());
        ExpenseReportFilterDTO filter = buildFilter(dateFrom, dateTo, status, paidTo, categoryName, employeeName);
        return ResponseEntity.ok(reportService.buildReportPage(filter, pageable));
    }

    // ----------------------------------------------------------------
    // 2. Summary only
    // ----------------------------------------------------------------

    @GetMapping("/summary")
    public ResponseEntity<ExpenseReportDTO> getSummary(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,

            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paidTo,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String employeeName
    ) {
        ExpenseReportFilterDTO filter = buildFilter(dateFrom, dateTo, status, paidTo, categoryName, employeeName);
        return ResponseEntity.ok(reportService.buildSummary(filter));
    }

    // ----------------------------------------------------------------
    // 3. Excel export
    // ----------------------------------------------------------------

    @GetMapping("/excel")
    public void exportExcel(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,

            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paidTo,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String employeeName,

            HttpServletResponse response
    ) throws IOException {
        validateDateRange(dateFrom, dateTo);
        ExpenseReportFilterDTO filter = buildFilter(dateFrom, dateTo, status, paidTo, categoryName, employeeName);
        excelExporter.export(reportService.buildReport(filter), response);
    }

    // ----------------------------------------------------------------
    // 4. PDF export
    // ----------------------------------------------------------------

    @GetMapping("/pdf")
    public void exportPdf(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,

            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paidTo,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String employeeName,

            HttpServletResponse response
    ) throws JRException, IOException {
        validateDateRange(dateFrom, dateTo);
        ExpenseReportFilterDTO filter = buildFilter(dateFrom, dateTo, status, paidTo, categoryName, employeeName);
        ExpenseReportDTO report = reportService.buildReport(filter);

        if (report.getRows().size() > PDF_ROW_LIMIT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "PDF export is limited to " + PDF_ROW_LIMIT + " rows. " +
                    "Your query returned " + report.getRows().size() + " rows. " +
                    "Please narrow the date range or use Excel export instead.");
        }

        pdfExporter.export(report, response);
    }

    // ================================================================
    // HELPERS
    // ================================================================

    private ExpenseReportFilterDTO buildFilter(LocalDate dateFrom, LocalDate dateTo,
                                                String status, String paidTo,
                                                String categoryName, String employeeName) {
        ExpenseReportFilterDTO f = new ExpenseReportFilterDTO();
        f.setDateFrom(dateFrom);
        f.setDateTo(dateTo);
        f.setPaidTo(paidTo);
        f.setCategoryName(categoryName);
        f.setEmployeeName(employeeName);
        if (status != null && !status.isBlank()) {
            try {
                f.setStatus(com.monaum.Rapid_Global.enums.Status.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException ignored) { }
        }
        return f;
    }

    private void validateDateRange(LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom == null || dateTo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "dateFrom and dateTo are required for export endpoints.");
        }
        if (dateTo.isBefore(dateFrom)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "dateTo must not be before dateFrom.");
        }
        if (dateFrom.plusDays(MAX_EXPORT_DAYS).isBefore(dateTo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Export date range cannot exceed " + MAX_EXPORT_DAYS + " days.");
        }
    }
}