package com.monaum.Rapid_Global.module.report.salesReport.newReport;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 */
@Component
public class SalesExcelExporter {

    /** Rows kept in memory before streaming to disk temp file */
    private static final int ROW_WINDOW = 500;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    // Colours
    private static final byte[] C_NAVY    = hex("1F3864");
    private static final byte[] C_WHITE   = hex("FFFFFF");
    private static final byte[] C_LIGHT   = hex("DCE6F1");
    private static final byte[] C_ALT     = hex("F2F2F2");
    private static final byte[] C_GRN_FG  = hex("375623");
    private static final byte[] C_RED_FG  = hex("9C0006");
    private static final byte[] C_GRN_BG  = hex("C6EFCE");
    private static final byte[] C_RED_BG  = hex("FFC7CE");
    private static final byte[] C_YEL_BG  = hex("FFEB9C");
    private static final byte[] C_YEL_FG  = hex("9C6500");

    // ----------------------------------------------------------------
    // PUBLIC API
    // ----------------------------------------------------------------

    public void export(SalesReportDTO report, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String filename = "Sales_Report_" + System.currentTimeMillis() + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        /*
         * SXSSFWorkbook wraps an XSSFWorkbook internally.
         * ROW_WINDOW = max rows kept in heap per sheet.
         * setCompressTempFiles(true) = gzip the temp spill file → lower disk I/O.
         */
        XSSFWorkbook xssfWb = new XSSFWorkbook();
        try (SXSSFWorkbook wb = new SXSSFWorkbook(xssfWb, ROW_WINDOW)) {
            wb.setCompressTempFiles(true);

            // Pre-build all styles against the underlying XSSFWorkbook
            // (SXSSF shares styles with its XSSFWorkbook backing store)
            StyleBundle styles = new StyleBundle(xssfWb);

            writeSummarySheet(wb, xssfWb, styles, report);
            writeDetailsSheet(wb, xssfWb, styles, report);

            wb.write(response.getOutputStream());
        }
        // SXSSFWorkbook.close() deletes the temp files automatically
    }

    // ================================================================
    // SHEET 1 — SUMMARY  (small, no streaming concern)
    // ================================================================

    private void writeSummarySheet(SXSSFWorkbook wb, XSSFWorkbook xssfWb,
                                    StyleBundle st, SalesReportDTO r) {
        SXSSFSheet sheet = wb.createSheet("Summary");
        sheet.trackAllColumnsForAutoSizing();
        sheet.setColumnWidth(0, 5500);
        sheet.setColumnWidth(1, 7000);

        int row = 0;
        row = mergedHeader(sheet, st.header, row, "RAPID GLOBAL — Sales Report", 2);
        row = mergedHeader(sheet, st.subHeader, row, dateRangeLabel(r), 2);
        row++; // spacer

        row = statCard(sheet, st, row, "Total Orders",   String.valueOf(r.getTotalOrders()),  C_NAVY,   C_LIGHT);
        row = statCard(sheet, st, row, "Total Revenue",  fmt(r.getTotalAmount()),              C_NAVY,   C_LIGHT);
        row = statCard(sheet, st, row, "Total Paid",     fmt(r.getTotalPaid()),                C_GRN_FG, C_GRN_BG);
        row = statCard(sheet, st, row, "Total Due",      fmt(r.getTotalDue()),                 C_RED_FG, C_RED_BG);
        row = statCard(sheet, st, row, "Total Discount", fmt(r.getTotalDiscount()),            C_NAVY,   C_LIGHT);
        row = statCard(sheet, st, row, "Total VAT",      fmt(r.getTotalVat()),                 C_NAVY,   C_LIGHT);
        row++;

        row = mergedHeader(sheet, st.header, row, "Status Breakdown", 2);
        tableHeader(sheet, st.tableHeader, row++, List.of("Status", "Count"), 2);

        for (Map.Entry<String, Long> e : r.getCountByStatus().entrySet()) {
            SXSSFRow r2 = sheet.createRow(row++);
            cell(r2, 0, e.getKey(),                st.plain);
            cell(r2, 1, e.getValue().toString(),   st.plain);
        }
    }

    // ================================================================
    // SHEET 2 — DETAILS  (streaming — safe at 40k rows)
    // ================================================================

    private void writeDetailsSheet(SXSSFWorkbook wb, XSSFWorkbook xssfWb,
                                    StyleBundle st, SalesReportDTO r) {
        SXSSFSheet sheet = wb.createSheet("Sales Details");

        // Only track columns we need to auto-size (tracking is expensive at scale)
        // Instead set fixed widths — much faster for large sheets
        int[] widths = {3500, 5000, 7000, 4500, 4500, 4500, 3000,
                         5500, 5500, 5500, 5500, 5500, 5500, 4500};
        for (int i = 0; i < widths.length; i++) sheet.setColumnWidth(i, widths[i]);

        int rowIdx = 0;

        // ---- Headers (written before any data so merges work) ----
        rowIdx = mergedHeader(sheet, st.header, rowIdx,
                "RAPID GLOBAL — Sales Detail Report", widths.length);
        rowIdx = mergedHeader(sheet, st.subHeader, rowIdx, dateRangeLabel(r), widths.length);
        rowIdx++;

        List<String> cols = List.of(
                "SL", "Invoice No", "Customer", "Phone",
                "Sell Date", "Delivery Date", "Items",
                "Sub Total", "Discount", "VAT",
                "Total", "Paid", "Due", "Status");
        int headerRowIdx = rowIdx;
        tableHeader(sheet, st.tableHeader, rowIdx++, cols, cols.size());

        // Freeze panes (freeze above the data rows)
        sheet.createFreezePane(0, rowIdx);

        // ---- Data rows (streamed — each row flushed after ROW_WINDOW) ----
        int sl = 1;
        boolean alt = false;

        for (SalesReportRowDTO row : r.getRows()) {
            SXSSFRow xRow = sheet.createRow(rowIdx++);
            xRow.setHeightInPoints(18);
            CellStyle base = alt ? st.alt : st.plain;

            cell  (xRow, 0,  String.valueOf(sl++),                                            base);
            cell  (xRow, 1,  row.getInvoiceNo(),                                               base);
            cell  (xRow, 2,  row.getCustomerName(),                                            base);
            cell  (xRow, 3,  row.getPhone(),                                                   base);
            cell  (xRow, 4,  row.getSellDate()     != null ? row.getSellDate().format(DATE_FMT)     : "", base);
            cell  (xRow, 5,  row.getDeliveryDate() != null ? row.getDeliveryDate().format(DATE_FMT) : "", base);
            cell  (xRow, 6,  String.valueOf(row.getItemCount()),                               base);
            numCell(xRow, 7,  row.getSubTotal(),    st.number(xssfWb, base));
            numCell(xRow, 8,  row.getDiscount(),    st.number(xssfWb, base));
            numCell(xRow, 9,  row.getVat(),         st.number(xssfWb, base));
            numCell(xRow, 10, row.getTotalAmount(),  st.number(xssfWb, base));
            numCell(xRow, 11, row.getPaidAmount(),   st.number(xssfWb, base));
            numCell(xRow, 12, row.getDueAmount(),    st.number(xssfWb, base));
            cell  (xRow, 13, row.getStatus().name(), st.status(xssfWb, row.getStatus().name()));

            alt = !alt;
        }

        // ---- Totals row ----
        SXSSFRow total = sheet.createRow(rowIdx);
        total.setHeightInPoints(20);
        cell(total, 0, "",       st.totalStyle);
        cell(total, 1, "TOTALS", st.totalStyle);
        for (int c = 2; c <= 6; c++) cell(total, c, "", st.totalStyle);
        numCell(total, 7,  r.getTotalSubAmount(), st.totalNum(xssfWb));
        numCell(total, 8,  r.getTotalDiscount(),  st.totalNum(xssfWb));
        numCell(total, 9,  r.getTotalVat(),       st.totalNum(xssfWb));
        numCell(total, 10, r.getTotalAmount(),    st.totalNum(xssfWb));
        numCell(total, 11, r.getTotalPaid(),      st.totalNum(xssfWb));
        numCell(total, 12, r.getTotalDue(),       st.totalNum(xssfWb));
        cell(total, 13, "",                       st.totalStyle);

        // Auto-filter on header row
        // Note: SXSSF supports setAutoFilter as long as range is declared
        // before rows in that range are flushed — safe here since we declared
        // the range after writing the header row.
        sheet.setAutoFilter(new CellRangeAddress(headerRowIdx, rowIdx, 0, cols.size() - 1));
    }

    // ================================================================
    // ROW HELPERS
    // ================================================================

    private int mergedHeader(SXSSFSheet sheet, CellStyle style,
                              int rowIdx, String text, int span) {
        SXSSFRow row = sheet.createRow(rowIdx);
        row.setHeightInPoints(26);
        SXSSFCell first = row.createCell(0);
        first.setCellValue(text);
        first.setCellStyle(style);
        for (int c = 1; c < span; c++) row.createCell(c).setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, span - 1));
        return rowIdx + 1;
    }

    private void tableHeader(SXSSFSheet sheet, CellStyle style,
                              int rowIdx, List<String> labels, int count) {
        SXSSFRow row = sheet.createRow(rowIdx);
        row.setHeightInPoints(20);
        for (int i = 0; i < count; i++) {
            SXSSFCell c = row.createCell(i);
            c.setCellValue(i < labels.size() ? labels.get(i) : "");
            c.setCellStyle(style);
        }
    }

    private int statCard(SXSSFSheet sheet, StyleBundle st, int rowIdx,
                          String label, String value, byte[] fg, byte[] bg) {
        SXSSFRow row = sheet.createRow(rowIdx);
        row.setHeightInPoints(22);

        XSSFCellStyle labelSt = buildStatStyle(sheet, fg, bg, false);
        XSSFCellStyle valueSt = buildStatStyle(sheet, fg, bg, true);

        SXSSFCell lc = row.createCell(0); lc.setCellValue(label); lc.setCellStyle(labelSt);
        SXSSFCell vc = row.createCell(1); vc.setCellValue(value); vc.setCellStyle(valueSt);
        return rowIdx + 1;
    }

    private XSSFCellStyle buildStatStyle(SXSSFSheet sheet, byte[] fg, byte[] bg, boolean bold) {
        // Access the underlying XSSFWorkbook through the sheet
        XSSFWorkbook xwb = (XSSFWorkbook) ((SXSSFWorkbook) sheet.getWorkbook()).getXSSFWorkbook();
        XSSFCellStyle s = xwb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(bg, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(bold ? HorizontalAlignment.RIGHT : HorizontalAlignment.LEFT);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorders(s, BorderStyle.THIN);
        XSSFFont f = xwb.createFont();
        if (bold) f.setFontHeightInPoints((short) 12);
        f.setBold(true);
        f.setColor(new XSSFColor(fg, null));
        s.setFont(f);
        return s;
    }

    private void cell(Row row, int col, String value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value != null ? value : "");
        c.setCellStyle(style);
    }

    private void numCell(Row row, int col, BigDecimal value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value != null ? value.doubleValue() : 0.0);
        c.setCellStyle(style);
    }

    // ================================================================
    // STYLE BUNDLE — all styles created once on the XSSFWorkbook
    // ================================================================

    private class StyleBundle {
        final XSSFCellStyle header;
        final XSSFCellStyle subHeader;
        final XSSFCellStyle tableHeader;
        final XSSFCellStyle plain;
        final XSSFCellStyle alt;
        final XSSFCellStyle totalStyle;
        private final DataFormat dataFormat;

        StyleBundle(XSSFWorkbook wb) {
            dataFormat = wb.createDataFormat();

            header = wb.createCellStyle();
            header.setFillForegroundColor(new XSSFColor(C_NAVY, null));
            header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            header.setAlignment(HorizontalAlignment.CENTER);
            header.setVerticalAlignment(VerticalAlignment.CENTER);
            setBorders(header, BorderStyle.THIN);
            XSSFFont hf = wb.createFont(); hf.setBold(true); hf.setFontHeightInPoints((short) 14);
            hf.setColor(new XSSFColor(C_WHITE, null)); header.setFont(hf);

            subHeader = wb.createCellStyle();
            subHeader.setFillForegroundColor(new XSSFColor(C_LIGHT, null));
            subHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            subHeader.setAlignment(HorizontalAlignment.CENTER);
            XSSFFont shf = wb.createFont(); shf.setItalic(true); subHeader.setFont(shf);

            tableHeader = wb.createCellStyle();
            tableHeader.setFillForegroundColor(new XSSFColor(C_NAVY, null));
            tableHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            tableHeader.setAlignment(HorizontalAlignment.CENTER);
            tableHeader.setVerticalAlignment(VerticalAlignment.CENTER);
            setBorders(tableHeader, BorderStyle.THIN);
            XSSFFont thf = wb.createFont(); thf.setBold(true); thf.setFontHeightInPoints((short) 10);
            thf.setColor(new XSSFColor(C_WHITE, null)); tableHeader.setFont(thf);

            plain = wb.createCellStyle();
            plain.setAlignment(HorizontalAlignment.CENTER);
            plain.setVerticalAlignment(VerticalAlignment.CENTER);
            setBorders(plain, BorderStyle.THIN);

            alt = wb.createCellStyle();
            alt.cloneStyleFrom(plain);
            alt.setFillForegroundColor(new XSSFColor(C_ALT, null));
            alt.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            totalStyle = wb.createCellStyle();
            totalStyle.setFillForegroundColor(new XSSFColor(C_NAVY, null));
            totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalStyle.setAlignment(HorizontalAlignment.RIGHT);
            totalStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            setBorders(totalStyle, BorderStyle.MEDIUM);
            XSSFFont tf = wb.createFont(); tf.setBold(true); tf.setFontHeightInPoints((short) 10);
            tf.setColor(new XSSFColor(C_WHITE, null)); totalStyle.setFont(tf);
        }

        /** Numeric style — cloned from base (plain or alt) + number format */
        XSSFCellStyle number(XSSFWorkbook wb, CellStyle base) {
            XSSFCellStyle s = wb.createCellStyle();
            s.cloneStyleFrom(base);
            s.setAlignment(HorizontalAlignment.RIGHT);
            s.setDataFormat(dataFormat.getFormat("#,##0.00"));
            return s;
        }

        /** Total numeric style */
        XSSFCellStyle totalNum(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle();
            s.cloneStyleFrom(totalStyle);
            s.setDataFormat(dataFormat.getFormat("#,##0.00"));
            return s;
        }

        /** Status badge style */
        XSSFCellStyle status(XSSFWorkbook wb, String statusName) {
            XSSFCellStyle s = wb.createCellStyle();
            s.cloneStyleFrom(plain);
            XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 9);
            switch (statusName.toUpperCase()) {
                case "COMPLETED" -> { s.setFillForegroundColor(new XSSFColor(C_GRN_BG, null)); f.setColor(new XSSFColor(C_GRN_FG, null)); }
                case "CANCELLED" -> { s.setFillForegroundColor(new XSSFColor(C_RED_BG, null)); f.setColor(new XSSFColor(C_RED_FG, null)); }
                default          -> { s.setFillForegroundColor(new XSSFColor(C_YEL_BG, null)); f.setColor(new XSSFColor(C_YEL_FG, null)); }
            }
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            s.setFont(f);
            return s;
        }
    }

    // ================================================================
    // UTILITIES
    // ================================================================

    private void setBorders(CellStyle s, BorderStyle bs) {
        s.setBorderTop(bs); s.setBorderBottom(bs);
        s.setBorderLeft(bs); s.setBorderRight(bs);
    }

    private String fmt(BigDecimal v) { return v != null ? String.format("%,.2f", v) : "0.00"; }

    private String dateRangeLabel(SalesReportDTO r) {
        String from = r.getDateFrom() != null ? r.getDateFrom().format(DATE_FMT) : "Beginning";
        String to   = r.getDateTo()   != null ? r.getDateTo().format(DATE_FMT)   : "Today";
        return "Period: " + from + " → " + to + "   |   Status: " + r.getStatusFilter();
    }

    private static byte[] hex(String h) {
        byte[] b = new byte[h.length() / 2];
        for (int i = 0; i < b.length; i++)
            b[i] = (byte)((Character.digit(h.charAt(i*2),16) << 4)
                         + Character.digit(h.charAt(i*2+1),16));
        return b;
    }
}