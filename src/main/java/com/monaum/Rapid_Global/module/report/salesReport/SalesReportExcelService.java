package com.monaum.Rapid_Global.module.report.salesReport;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * @since 04-Feb-26 11:33 PM
 */

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * Excel Export Service for Sales Reports
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SalesReportExcelService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    /**
     * Export sales report to Excel
     */
    public byte[] exportSalesReportToExcel(SalesReportResponseDTO reportData, String reportTitle) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        // Create Summary Sheet
        createSummarySheet(workbook, reportData);

        // Create Details Sheet
        createDetailsSheet(workbook, reportData);

        // Create Grouped Data Sheet (if available)
        if (reportData.getGroupedData() != null && !reportData.getGroupedData().isEmpty()) {
            createGroupedDataSheet(workbook, reportData);
        }

        // Write to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    /**
     * Create summary sheet
     */
    private void createSummarySheet(Workbook workbook, SalesReportResponseDTO reportData) {
        Sheet sheet = workbook.createSheet("Summary");

        // Create styles
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);

        int rowNum = 0;

        // Title
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("SALES REPORT SUMMARY");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        rowNum++;

        SalesReportResponseDTO.SalesSummary summary = reportData.getSummary();

        // Overall Statistics
        addSummaryRow(sheet, rowNum++, "Total Orders", summary.getTotalOrders(), null, headerStyle);
        addSummaryRow(sheet, rowNum++, "Total Revenue", null, summary.getTotalRevenue(), currencyStyle);
        addSummaryRow(sheet, rowNum++, "Total Discount", null, summary.getTotalDiscount(), currencyStyle);
        addSummaryRow(sheet, rowNum++, "Total VAT", null, summary.getTotalVat(), currencyStyle);
        addSummaryRow(sheet, rowNum++, "Net Revenue", null, summary.getNetRevenue(), currencyStyle);
        addSummaryRow(sheet, rowNum++, "Average Order Value", null, summary.getAverageOrderValue(), currencyStyle);
        addSummaryRow(sheet, rowNum++, "Total Items Sold", summary.getTotalItemsSold(), null, headerStyle);
        addSummaryRow(sheet, rowNum++, "Total Customers", summary.getTotalCustomers(), null, headerStyle);
        rowNum++;

        // Status-wise breakdown
        Row statusHeaderRow = sheet.createRow(rowNum++);
        Cell statusHeaderCell = statusHeaderRow.createCell(0);
        statusHeaderCell.setCellValue("STATUS BREAKDOWN");
        statusHeaderCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 3));

        addSummaryRow(sheet, rowNum++, "Pending Orders", summary.getPendingOrders(), summary.getPendingAmount(), currencyStyle);
        addSummaryRow(sheet, rowNum++, "Confirmed Orders", summary.getConfirmedOrders(), summary.getConfirmedAmount(), currencyStyle);
        addSummaryRow(sheet, rowNum++, "Shipped Orders", summary.getShippedOrders(), null, headerStyle);
        addSummaryRow(sheet, rowNum++, "Delivered Orders", summary.getDeliveredOrders(), summary.getDeliveredAmount(), currencyStyle);
        addSummaryRow(sheet, rowNum++, "Cancelled Orders", summary.getCancelledOrders(), summary.getCancelledAmount(), currencyStyle);

        // Auto-size columns
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Create details sheet
     */
    private void createDetailsSheet(Workbook workbook, SalesReportResponseDTO reportData) {
        Sheet sheet = workbook.createSheet("Sales Details");

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Invoice No", "Date", "Customer", "Phone", "Company",
                "Status", "Items", "Subtotal", "Discount", "VAT", "Total", "Paid", "Due"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Add data rows
        int rowNum = 1;
        for (SalesReportResponseDTO.SalesDetailDTO detail : reportData.getSalesDetails()) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(detail.getInvoiceNo());

            Cell dateCell = row.createCell(1);
            if (detail.getSellDate() != null) {
                dateCell.setCellValue(detail.getSellDate().format(DATE_FORMATTER));
            }
            dateCell.setCellStyle(dateStyle);

            row.createCell(2).setCellValue(detail.getCustomerName());
            row.createCell(3).setCellValue(detail.getPhone());
            row.createCell(4).setCellValue(detail.getCompanyName() != null ? detail.getCompanyName() : "");
            row.createCell(5).setCellValue(detail.getStatus());
            row.createCell(6).setCellValue(detail.getTotalItems());

            createCurrencyCell(row, 7, detail.getSubtotal(), currencyStyle);
            createCurrencyCell(row, 8, detail.getDiscount(), currencyStyle);
            createCurrencyCell(row, 9, detail.getVat(), currencyStyle);
            createCurrencyCell(row, 10, detail.getTotalAmount(), currencyStyle);
            createCurrencyCell(row, 11, detail.getPaidAmount(), currencyStyle);
            createCurrencyCell(row, 12, detail.getDueAmount(), currencyStyle);
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Create grouped data sheet
     */
    private void createGroupedDataSheet(Workbook workbook, SalesReportResponseDTO reportData) {
        Sheet sheet = workbook.createSheet("Grouped Analysis");

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Period", "Order Count", "Total Revenue", "Avg Order Value"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Add data rows
        int rowNum = 1;
        for (SalesReportResponseDTO.GroupedSalesDTO grouped : reportData.getGroupedData()) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(grouped.getGroupLabel());
            row.createCell(1).setCellValue(grouped.getOrderCount());
            createCurrencyCell(row, 2, grouped.getTotalRevenue(), currencyStyle);
            createCurrencyCell(row, 3, grouped.getAverageOrderValue(), currencyStyle);
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Helper method to add summary row
     */
    private void addSummaryRow(Sheet sheet, int rowNum, String label, Integer intValue,
                               java.math.BigDecimal decimalValue, CellStyle valueStyle) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(label);

        Cell valueCell = row.createCell(1);
        if (intValue != null) {
            valueCell.setCellValue(intValue);
        } else if (decimalValue != null) {
            valueCell.setCellValue(decimalValue.doubleValue());
            valueCell.setCellStyle(valueStyle);
        }
    }

    /**
     * Create currency cell
     */
    private void createCurrencyCell(Row row, int column, java.math.BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
            cell.setCellStyle(style);
        }
    }

    /**
     * Create header style
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    /**
     * Create title style
     */
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    /**
     * Create currency style
     */
    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));
        return style;
    }

    /**
     * Create date style
     */
    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }
}