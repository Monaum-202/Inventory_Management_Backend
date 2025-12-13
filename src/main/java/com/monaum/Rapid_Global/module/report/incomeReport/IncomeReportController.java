package com.monaum.Rapid_Global.module.report.incomeReport;

import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/income-reports")
@RequiredArgsConstructor
public class IncomeReportController {

    private final IncomeReportService incomeReportService;

    /**
     * Generate income incomeReport with filters
     * 
     * Example: GET /api/income-reports?startDate=2024-01-01&endDate=2024-12-31&status=APPROVED
     */
    @GetMapping
    public ResponseEntity<BaseApiResponseDTO<?>> getIncomeReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long paymentMethodId,
            @PageableDefault(size = 50) Pageable pageable
    ) {
        IncomeReportFilterDto filters = new IncomeReportFilterDto();
        filters.setStartDate(startDate);
        filters.setEndDate(endDate);
        filters.setCategoryId(categoryId);
        filters.setPaymentMethodId(paymentMethodId);

        return incomeReportService.generateReport(filters, pageable);
    }

    /**
     * Get income incomeReport summary/analytics
     * 
     * Example: GET /api/income-reports/summary?startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/summary")
    public ResponseEntity<BaseApiResponseDTO<?>> getReportSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        // Default to current month if dates not provided
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        return incomeReportService.getReportSummary(startDate, endDate);
    }

    /**
     * Get daily income trend
     * 
     * Example: GET /api/income-reports/trend?startDate=2024-01-01&endDate=2024-01-31
     */
    @GetMapping("/trend")
    public ResponseEntity<BaseApiResponseDTO<?>> getDailyTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return incomeReportService.getDailyTrend(startDate, endDate);
    }

    /**
     * Export income incomeReport as CSV
     * 
     * Example: GET /api/income-reports/export?startDate=2024-01-01&endDate=2024-12-31&format=csv
     */
    @GetMapping("/export")
    public ResponseEntity<?> exportReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long paymentMethodId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paidFrom,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) Long salesId,
            @RequestParam(defaultValue = "csv") String format
    ) {
        IncomeReportFilterDto filters = new IncomeReportFilterDto();
        filters.setStartDate(startDate);
        filters.setEndDate(endDate);
        filters.setCategoryId(categoryId);
        filters.setPaymentMethodId(paymentMethodId);
        filters.setStatus(status);
        filters.setPaidFrom(paidFrom);
        filters.setMinAmount(minAmount);
        filters.setMaxAmount(maxAmount);
        filters.setSalesId(salesId);

        List<IncomeReportResDto> reportData = incomeReportService.exportReport(filters);

        if ("csv".equalsIgnoreCase(format)) {
            String csv = convertToCSV(reportData);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "income-incomeReport.csv");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csv);
        }

        // Default: return JSON
        return ResponseEntity.ok(reportData);
    }

    /**
     * Convert incomeReport data to CSV format
     */
    private String convertToCSV(List<IncomeReportResDto> data) {
        StringBuilder csv = new StringBuilder();
        
        // Header
        csv.append("Income ID,Category,Amount,Payment Method,Paid From,Company,Income Date,Description,Status,Approved By,Created By,Sales Invoice,Cancel Reason\n");
        
        // Data rows
        for (IncomeReportResDto row : data) {
            csv.append(escapeCSV(row.getIncomeId())).append(",");
            csv.append(escapeCSV(row.getCategoryName())).append(",");
            csv.append(row.getAmount() != null ? row.getAmount() : "").append(",");
            csv.append(escapeCSV(row.getPaymentMethodName())).append(",");
            csv.append(escapeCSV(row.getPaidFrom())).append(",");
            csv.append(escapeCSV(row.getPaidFromCompany())).append(",");
            csv.append(row.getIncomeDate() != null ? row.getIncomeDate() : "").append(",");
            csv.append(escapeCSV(row.getDescription())).append(",");
            csv.append(escapeCSV(row.getStatus())).append(",");
            csv.append(escapeCSV(row.getApprovedByName())).append(",");
            csv.append(escapeCSV(row.getCreatedByName())).append(",");
            csv.append(escapeCSV(row.getSalesInvoiceNo())).append(",");
            csv.append(escapeCSV(row.getCancelReason()));
            csv.append("\n");
        }
        
        return csv.toString();
    }

    /**
     * Escape special characters for CSV
     */
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        
        return value;
    }
}