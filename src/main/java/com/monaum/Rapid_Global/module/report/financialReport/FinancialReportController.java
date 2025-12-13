package com.monaum.Rapid_Global.module.report.financialReport;

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

/**
 * Controller for combined financial reports (Income + Expense)
 */
@RestController
@RequestMapping("/api/financial-reports")
@RequiredArgsConstructor
public class FinancialReportController {

    private final FinancialReportService financialReportService;

    /**
     * Generate combined financial report
     * 
     * Example: GET /api/financial-reports?startDate=2024-01-01&endDate=2024-12-31&transactionType=ALL
     */
    @GetMapping
    public ResponseEntity<BaseApiResponseDTO<?>> getFinancialReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long paymentMethodId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String transactionType, // INCOME, EXPENSE, ALL
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @PageableDefault(size = 50) Pageable pageable
    ) {
        FinancialReportFilterDto filters = new FinancialReportFilterDto();
        filters.setStartDate(startDate);
        filters.setEndDate(endDate);
        filters.setCategoryId(categoryId);
        filters.setPaymentMethodId(paymentMethodId);
        filters.setStatus(status);
        filters.setTransactionType(transactionType);
        filters.setSearchTerm(searchTerm);
        filters.setMinAmount(minAmount);
        filters.setMaxAmount(maxAmount);

        return financialReportService.generateReport(filters, pageable);
    }

    /**
     * Get financial summary with income, expense, and net profit
     * 
     * Example: GET /api/financial-reports/summary?startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/summary")
    public ResponseEntity<BaseApiResponseDTO<?>> getFinancialSummary(
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

        return financialReportService.getFinancialSummary(startDate, endDate);
    }

    /**
     * Get financial trend (income vs expense over time)
     * 
     * Example: GET /api/financial-reports/trend?startDate=2024-01-01&endDate=2024-01-31
     */
    @GetMapping("/trend")
    public ResponseEntity<BaseApiResponseDTO<?>> getFinancialTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return financialReportService.getFinancialTrend(startDate, endDate);
    }

    /**
     * Export financial report as CSV
     * 
     * Example: GET /api/financial-reports/export?startDate=2024-01-01&endDate=2024-12-31&format=csv
     */
    @GetMapping("/export")
    public ResponseEntity<?> exportReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long paymentMethodId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "ALL") String transactionType,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(defaultValue = "csv") String format
    ) {
        FinancialReportFilterDto filters = new FinancialReportFilterDto();
        filters.setStartDate(startDate);
        filters.setEndDate(endDate);
        filters.setCategoryId(categoryId);
        filters.setPaymentMethodId(paymentMethodId);
        filters.setStatus(status);
        filters.setTransactionType(transactionType);
        filters.setSearchTerm(searchTerm);
        filters.setMinAmount(minAmount);
        filters.setMaxAmount(maxAmount);

        List<FinancialTransactionDto> reportData = financialReportService.exportReport(filters);

        if ("csv".equalsIgnoreCase(format)) {
            String csv = convertToCSV(reportData);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "financial-report.csv");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csv);
        }

        return ResponseEntity.ok(reportData);
    }

    /**
     * Convert report data to CSV format
     */
    private String convertToCSV(List<FinancialTransactionDto> data) {
        StringBuilder csv = new StringBuilder();
        
        // Header
        csv.append("Transaction ID,Type,Category,Amount,Payment Method,Counterparty,Company,Date,Description,Status,Approved By,Created By,Sales Invoice,Cancel Reason\n");
        
        // Data rows
        for (FinancialTransactionDto row : data) {
            csv.append(escapeCSV(row.getTransactionId())).append(",");
            csv.append(escapeCSV(row.getTransactionType())).append(",");
            csv.append(escapeCSV(row.getCategoryName())).append(",");
            csv.append(row.getAmount() != null ? row.getAmount() : "").append(",");
            csv.append(escapeCSV(row.getPaymentMethodName())).append(",");
            csv.append(escapeCSV(row.getCounterparty())).append(",");
            csv.append(escapeCSV(row.getCounterpartyCompany())).append(",");
            csv.append(row.getTransactionDate() != null ? row.getTransactionDate() : "").append(",");
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