package com.monaum.Rapid_Global.module.report.salesReport.newReport;


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
 * KEY FIXES:
 *  1. GET /api/reports/sales  — now paginated (never loads all rows)
 *  2. GET /api/reports/sales/pdf — hard cap at PDF_ROW_LIMIT rows
 *  3. GET /api/reports/sales/excel — no row cap (streaming handles it)
 *  4. Mandatory date range on bulk exports (prevents accidental full-table scans)
 *
 * ENDPOINTS:
 *   GET /api/reports/sales               → paginated JSON
 *   GET /api/reports/sales/summary       → totals only (no rows)
 *   GET /api/reports/sales/excel         → .xlsx download (streaming)
 *   GET /api/reports/sales/pdf           → .pdf download (max PDF_ROW_LIMIT rows)
 */
@RestController
@RequestMapping("/api/reports/sales")
@RequiredArgsConstructor
public class SalesReportController {

    /**
     * PDF is not suitable for very large datasets.
     * A 5 000-row PDF is already ~100 pages — hard to use.
     * Callers should use Excel for large exports.
     */
    private static final int PDF_ROW_LIMIT = 5_000;

    /**
     * Safety guard: require a date range for bulk exports so a single
     * misconfigured call cannot trigger a full-table scan.
     * Set to 366 days (allow full-year exports).
     */
    private static final int MAX_EXPORT_DAYS = 366;

    private final SalesReportService reportService;
    private final SalesExcelExporter excelExporter;
    private final SalesPdfExporter   pdfExporter;

    // ----------------------------------------------------------------
    // 1. Paginated JSON  — safe for frontend dashboards / tables
    // ----------------------------------------------------------------

    @GetMapping
    public ResponseEntity<Page<SalesReportRowDTO>> getReport(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,

            @RequestParam(required = false) String status,
            @RequestParam(required = false) String customerName,

            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "50")  int size   // max 200 enforced below
    ) {
        size = Math.min(size, 200); // prevent oversized page requests
        Pageable pageable = PageRequest.of(page, size, Sort.by("sellDate").descending());

        SalesReportFilterDTO filter = buildFilter(dateFrom, dateTo, status, customerName);
        Page<SalesReportRowDTO> result = reportService.buildReportPage(filter, pageable);
        return ResponseEntity.ok(result);
    }

    // ----------------------------------------------------------------
    // 2. Summary only (totals + status breakdown, zero rows)
    //    Cheap — no item/payment joins needed at the API level
    // ----------------------------------------------------------------

    @GetMapping("/summary")
    public ResponseEntity<SalesReportDTO> getSummary(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,

            @RequestParam(required = false) String status,
            @RequestParam(required = false) String customerName
    ) {
        SalesReportFilterDTO filter = buildFilter(dateFrom, dateTo, status, customerName);
        // Build full report but strip the rows list before returning
        SalesReportDTO full = reportService.buildReport(filter);
        full.setRows(null); // don't send 40k rows over JSON
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
            @RequestParam(required = false) String customerName,

            HttpServletResponse response
    ) throws IOException {
        validateDateRange(dateFrom, dateTo);
        SalesReportFilterDTO filter = buildFilter(dateFrom, dateTo, status, customerName);
        SalesReportDTO report = reportService.buildReport(filter);
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
            @RequestParam(required = false) String customerName,

            HttpServletResponse response
    ) throws JRException, IOException {
        validateDateRange(dateFrom, dateTo);
        SalesReportFilterDTO filter = buildFilter(dateFrom, dateTo, status, customerName);
        SalesReportDTO report = reportService.buildReport(filter);

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

    private SalesReportFilterDTO buildFilter(LocalDate dateFrom, LocalDate dateTo,
                                              String status, String customerName) {
        SalesReportFilterDTO f = new SalesReportFilterDTO();
        f.setDateFrom(dateFrom);
        f.setDateTo(dateTo);
        f.setCustomerName(customerName);
        if (status != null && !status.isBlank()) {
            try {
                f.setStatus(com.monaum.Rapid_Global.enums.OrderStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException ignored) {
                // unrecognised status value → treat as "all" rather than throwing
            }
        }
        return f;
    }

    /**
     * Require a date range for export endpoints.
     * Prevents "SELECT *" over the entire Sales table on a production DB.
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