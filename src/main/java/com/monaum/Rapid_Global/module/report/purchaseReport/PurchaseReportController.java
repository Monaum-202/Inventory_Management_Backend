package com.monaum.Rapid_Global.module.report.purchaseReport;

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
 *   GET /api/reports/purchases               → paginated JSON
 *   GET /api/reports/purchases/summary       → totals only (no rows)
 *   GET /api/reports/purchases/excel         → .xlsx download (streaming)
 *   GET /api/reports/purchases/pdf           → .pdf download (max PDF_ROW_LIMIT rows)
 */
@RestController
@RequestMapping("/api/reports/purchases")
@RequiredArgsConstructor
public class PurchaseReportController {

    /**
     * PDF is not suitable for very large datasets.
     * 5 000 rows is already ~100 pages. Use Excel for large exports.
     */
    private static final int PDF_ROW_LIMIT = 5_000;

    /**
     * Require a date range on bulk exports to prevent full-table scans.
     * 366 days = allow a full-year export.
     */
    private static final int MAX_EXPORT_DAYS = 366;

    private final PurchaseReportService  reportService;
    private final PurchaseExcelExporter  excelExporter;
    private final PurchasePdfExporter    pdfExporter;

    // ----------------------------------------------------------------
    // 1. Paginated JSON  — safe for frontend dashboards / tables
    // ----------------------------------------------------------------

    @GetMapping
    public ResponseEntity<Page<PurchaseReportRowDTO>> getReport(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,

            @RequestParam(required = false) String status,
            @RequestParam(required = false) String supplierName,

            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "50")  int size
    ) {
        size = Math.min(size, 200);
        Pageable pageable = PageRequest.of(page, size, Sort.by("purchaseDate").descending());

        PurchaseReportFilterDTO filter = buildFilter(dateFrom, dateTo, status, supplierName);
        Page<PurchaseReportRowDTO> result = reportService.buildReportPage(filter, pageable);
        return ResponseEntity.ok(result);
    }

    // ----------------------------------------------------------------
    // 2. Summary only (totals + status breakdown, zero rows)
    // ----------------------------------------------------------------

    @GetMapping("/summary")
    public ResponseEntity<PurchaseReportDTO> getSummary(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,

            @RequestParam(required = false) String status,
            @RequestParam(required = false) String supplierName
    ) {
        PurchaseReportFilterDTO filter = buildFilter(dateFrom, dateTo, status, supplierName);
        PurchaseReportDTO full = reportService.buildReport(filter);
        full.setRows(null); // don't send thousands of rows over JSON
        return ResponseEntity.ok(full);
    }

    // ----------------------------------------------------------------
    // 3. Excel export  (streaming — any size)
    // ----------------------------------------------------------------

    @GetMapping("/excel")
    public void exportExcel(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,

            @RequestParam(required = false) String status,
            @RequestParam(required = false) String supplierName,

            HttpServletResponse response
    ) throws IOException {
        validateDateRange(dateFrom, dateTo);
        PurchaseReportFilterDTO filter = buildFilter(dateFrom, dateTo, status, supplierName);
        PurchaseReportDTO report = reportService.buildReport(filter);
        excelExporter.export(report, response);
    }

    // ----------------------------------------------------------------
    // 4. PDF export  (capped at PDF_ROW_LIMIT)
    // ----------------------------------------------------------------

    @GetMapping("/pdf")
    public void exportPdf(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,

            @RequestParam(required = false) String status,
            @RequestParam(required = false) String supplierName,

            HttpServletResponse response
    ) throws JRException, IOException {
        validateDateRange(dateFrom, dateTo);
        PurchaseReportFilterDTO filter = buildFilter(dateFrom, dateTo, status, supplierName);
        PurchaseReportDTO report = reportService.buildReport(filter);

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

    private PurchaseReportFilterDTO buildFilter(LocalDate dateFrom, LocalDate dateTo,
                                                 String status, String supplierName) {
        PurchaseReportFilterDTO f = new PurchaseReportFilterDTO();
        f.setDateFrom(dateFrom);
        f.setDateTo(dateTo);
        f.setSupplierName(supplierName);
        if (status != null && !status.isBlank()) {
            try {
                f.setStatus(com.monaum.Rapid_Global.enums.OrderStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException ignored) {
                // unrecognised status value → treat as "all"
            }
        }
        return f;
    }

    /**
     * Require a date range for export endpoints.
     * Prevents a full-table scan on the Purchase table.
     */
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
                    "Export date range cannot exceed " + MAX_EXPORT_DAYS + " days. " +
                    "Split into multiple exports if you need a longer period.");
        }
    }
}