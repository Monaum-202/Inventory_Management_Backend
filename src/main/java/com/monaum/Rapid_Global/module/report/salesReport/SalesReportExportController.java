//package com.monaum.Rapid_Global.module.report.salesReport;
//
//import com.monaum.Rapid_Global.enums.OrderStatus;
//import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//
///**
// * Monaum Hossain
// * monaum.202@gmail.com
// * Sales Report Export Controller
// */
//@Slf4j
//@RestController
//@RequestMapping("/api/sales-reports/export")
//@RequiredArgsConstructor
//@Tag(name = "Sales Report Export", description = "Export sales reports to Excel format")
//public class SalesReportExportController {
//
//    private final SalesReportService salesReportService;
//    private final SalesReportExcelService excelService;
//
//    /**
//     * Export sales report to Excel
//     */
//    @GetMapping("/excel")
//    @Operation(summary = "Export sales report to Excel",
//            description = "Download sales report as Excel file with multiple sheets")
//    public ResponseEntity<byte[]> exportToExcel(
//            @Parameter(description = "Start date (format: yyyy-MM-dd)")
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//
//            @Parameter(description = "End date (format: yyyy-MM-dd)")
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
//
//            @Parameter(description = "Filter by order status")
//            @RequestParam(required = false) OrderStatus status,
//
//            @Parameter(description = "Filter by customer ID")
//            @RequestParam(required = false) Long customerId,
//
//            @Parameter(description = "Filter by customer name (partial match)")
//            @RequestParam(required = false) String customerName,
//
//            @Parameter(description = "Group by: DAY, WEEK, MONTH, YEAR")
//            @RequestParam(required = false) String groupBy,
//
//            @Parameter(description = "Sort by field")
//            @RequestParam(defaultValue = "sellDate") String sortBy,
//
//            @Parameter(description = "Sort direction: ASC or DESC")
//            @RequestParam(defaultValue = "DESC") String sortDirection
//    ) throws IOException {
//        log.info("Exporting sales report to Excel - startDate: {}, endDate: {}", startDate, endDate);
//
//        // Build request
//        SalesReportRequestDTO request = new SalesReportRequestDTO();
//        request.setStartDate(startDate);
//        request.setEndDate(endDate);
//        request.setStatus(status);
//        request.setCustomerId(customerId);
//        request.setCustomerName(customerName);
//        request.setGroupBy(groupBy);
//        request.setPage(0);
//        request.setSize(10000); // Get all records for export
//        request.setSortBy(sortBy);
//        request.setSortDirection(sortDirection);
//
//        // Generate report
//        ResponseEntity<BaseApiResponseDTO<?>> response =
//                salesReportService.generateSalesReport(request);
//
//        BaseApiResponseDTO<?> apiResponse = response.getBody();
//
//        if (apiResponse == null || apiResponse.getData() == null) {
//            throw new RuntimeException("Failed to generate sales report");
//        }
//
//        SalesReportResponseDTO reportData =
//                (SalesReportResponseDTO) apiResponse.getData();
//
//
//        // Generate Excel
//        String reportTitle = String.format("Sales Report %s to %s",
//                startDate != null ? startDate.format(DateTimeFormatter.ISO_DATE) : "All",
//                endDate != null ? endDate.format(DateTimeFormatter.ISO_DATE) : "All"
//        );
//
//        byte[] excelBytes = excelService.exportSalesReportToExcel(reportData, reportTitle);
//
//        // Prepare response headers
//        String filename = String.format("Sales_Report_%s_%s.xlsx",
//                startDate != null ? startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) : "All",
//                endDate != null ? endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) : "All"
//        );
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        headers.setContentDispositionFormData("attachment", filename);
//        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .body(excelBytes);
//    }
//}
