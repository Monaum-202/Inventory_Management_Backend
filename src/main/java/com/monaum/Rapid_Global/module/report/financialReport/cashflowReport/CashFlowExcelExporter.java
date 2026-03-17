package com.monaum.Rapid_Global.module.report.financialReport.cashflowReport;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
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
import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * Sheet 1 — Cash Flow Statement
 * Sheet 2 — Monthly Trend (with running closing balance)
 * Sheet 3 — Category Detail (inflows vs outflows side-by-side)
 */
@Component
public class CashFlowExcelExporter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    private static final byte[] C_NAVY    = hex("1F3864");
    private static final byte[] C_WHITE   = hex("FFFFFF");
    private static final byte[] C_LIGHT   = hex("DCE6F1");
    private static final byte[] C_ALT     = hex("F2F2F2");
    private static final byte[] C_GRN_FG  = hex("375623");
    private static final byte[] C_GRN_BG  = hex("C6EFCE");
    private static final byte[] C_RED_FG  = hex("9C0006");
    private static final byte[] C_RED_BG  = hex("FFC7CE");
    private static final byte[] C_YEL_FG  = hex("9C6500");
    private static final byte[] C_YEL_BG  = hex("FFEB9C");
    private static final byte[] C_SECTION = hex("1A5276");

    // ----------------------------------------------------------------

    public void export(CashFlowReportDTO r, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"CashFlow_Report_" + System.currentTimeMillis() + ".xlsx\"");

        XSSFWorkbook xssfWb = new XSSFWorkbook();
        try (SXSSFWorkbook wb = new SXSSFWorkbook(xssfWb, 200)) {
            wb.setCompressTempFiles(true);
            Styles st = new Styles(xssfWb);
            writeCashFlowStatement(wb, xssfWb, st, r);
            writeMonthlySheet(wb, xssfWb, st, r);
            writeCategorySheet(wb, xssfWb, st, r);
            wb.write(response.getOutputStream());
        }
    }

    // ================================================================
    // SHEET 1 — CASH FLOW STATEMENT
    // ================================================================

    private void writeCashFlowStatement(SXSSFWorkbook wb, XSSFWorkbook xssfWb,
                                         Styles st, CashFlowReportDTO r) {
        SXSSFSheet sheet = wb.createSheet("Cash Flow Statement");
        sheet.setColumnWidth(0, 9000);
        sheet.setColumnWidth(1, 5500);
        sheet.setColumnWidth(2, 3500);

        int row = 0;
        row = bigTitle(sheet, xssfWb, row, "RAPID GLOBAL — CASH FLOW STATEMENT");
        row = periodRow(sheet, xssfWb, row, r);
        row++;

        for (CashFlowLineItemDTO item : r.getLineItems()) {
            String lt = item.getLineType();

            if (CashFlowReportService.LT_SECTION_HEADER.equals(lt)) {
                SXSSFRow xRow = sheet.createRow(row++);
                xRow.setHeightInPoints(22);
                XSSFCellStyle s = sectionStyle(xssfWb);
                cell(xRow, 0, item.getLabel(), s);
                cell(xRow, 1, "", s); cell(xRow, 2, "", s);
                sheet.addMergedRegion(new CellRangeAddress(row-1, row-1, 0, 2));

            } else if (CashFlowReportService.LT_CATEGORY.equals(lt)) {
                SXSSFRow xRow = sheet.createRow(row++);
                xRow.setHeightInPoints(18);
                boolean pos = Boolean.TRUE.equals(item.getPositive());
                cell   (xRow, 0, "    " + item.getLabel(), st.plain);
                numCell(xRow, 1, item.getAmount(), categoryNumStyle(xssfWb, pos ? C_GRN_FG : C_RED_FG));
                cell   (xRow, 2, item.getPercentage() != null ? item.getPercentage() + "%" : "", st.pct);

            } else if (CashFlowReportService.LT_SUBTOTAL.equals(lt)) {
                SXSSFRow xRow = sheet.createRow(row++);
                xRow.setHeightInPoints(20);
                boolean pos = Boolean.TRUE.equals(item.getPositive());
                XSSFCellStyle s = subtotalStyle(xssfWb, pos ? C_GRN_FG : C_RED_FG, pos ? C_GRN_BG : C_RED_BG);
                cell(xRow, 0, item.getLabel(), s);
                numCell(xRow, 1, item.getAmount(), s);
                cell(xRow, 2, "", s);

            } else if (CashFlowReportService.LT_SPACER.equals(lt)) {
                sheet.createRow(row++).setHeightInPoints(8);

            } else if (CashFlowReportService.LT_NET.equals(lt)) {
                SXSSFRow xRow = sheet.createRow(row++);
                xRow.setHeightInPoints(28);
                boolean surplus = Boolean.TRUE.equals(item.getPositive());
                XSSFCellStyle s = netStyle(xssfWb, surplus ? C_GRN_FG : C_RED_FG, surplus ? C_GRN_BG : C_RED_BG);
                cell(xRow, 0, item.getLabel(), s);
                numCell(xRow, 1, item.getAmount(), s);
                cell(xRow, 2, r.getNetFlowPct() != null ? r.getNetFlowPct() + "%" : "", s);
            }
        }

        row++;
        SXSSFRow note = sheet.createRow(row);
        cell(note, 0, "* Includes APPROVED transactions only.", st.note);
        sheet.addMergedRegion(new CellRangeAddress(row, row, 0, 2));
    }

    // ================================================================
    // SHEET 2 — MONTHLY TREND
    // ================================================================

    private void writeMonthlySheet(SXSSFWorkbook wb, XSSFWorkbook xssfWb,
                                    Styles st, CashFlowReportDTO r) {
        SXSSFSheet sheet = wb.createSheet("Monthly Trend");
        int[] widths = {4500, 5500, 5500, 5500, 5500};
        for (int i = 0; i < widths.length; i++) sheet.setColumnWidth(i, widths[i]);

        int row = 0;
        row = sectionMerge(sheet, xssfWb, row, "Monthly Cash Flow Trend", 5);
        row = periodRow(sheet, xssfWb, row, r);
        row++;

        SXSSFRow hdr = sheet.createRow(row++);
        hdr.setHeightInPoints(20);
        for (int i = 0; i < 5; i++) {
            String[] labels = {"Month", "Inflows", "Outflows", "Net Cash Flow", "Closing Balance"};
            tableHdrCell(hdr, i, labels[i], st.tableHdr);
        }

        boolean alt = false;
        BigDecimal sumIn = BigDecimal.ZERO, sumOut = BigDecimal.ZERO, sumNet = BigDecimal.ZERO;

        for (CashFlowMonthlyDTO m : r.getMonthlyBreakdown()) {
            SXSSFRow xRow = sheet.createRow(row++);
            xRow.setHeightInPoints(18);
            CellStyle base = alt ? st.alt : st.plain;

            cell   (xRow, 0, m.getMonthLabel(),    base);
            numCell(xRow, 1, m.getTotalInflow(),   st.numGreen(xssfWb, base));
            numCell(xRow, 2, m.getTotalOutflow(),  st.numRed(xssfWb,   base));
            boolean pos = m.getNetCashFlow().compareTo(BigDecimal.ZERO) >= 0;
            numCell(xRow, 3, m.getNetCashFlow().abs(), pos ? st.numGreen(xssfWb, base) : st.numRed(xssfWb, base));
            numCell(xRow, 4, m.getClosingBalance().abs(),
                    m.getClosingBalance().compareTo(BigDecimal.ZERO) >= 0
                            ? st.numGreen(xssfWb, base) : st.numRed(xssfWb, base));

            sumIn  = sumIn.add(m.getTotalInflow());
            sumOut = sumOut.add(m.getTotalOutflow());
            sumNet = sumNet.add(m.getNetCashFlow());
            alt    = !alt;
        }

        SXSSFRow tot = sheet.createRow(row);
        tot.setHeightInPoints(20);
        cell   (tot, 0, "TOTALS",     st.totalLabel);
        numCell(tot, 1, sumIn,        st.totalNum(xssfWb));
        numCell(tot, 2, sumOut,       st.totalNum(xssfWb));
        numCell(tot, 3, sumNet.abs(), st.totalNum(xssfWb));
        cell   (tot, 4, "",           st.totalLabel);
    }

    // ================================================================
    // SHEET 3 — CATEGORY DETAIL
    // ================================================================

    private void writeCategorySheet(SXSSFWorkbook wb, XSSFWorkbook xssfWb,
                                     Styles st, CashFlowReportDTO r) {
        SXSSFSheet sheet = wb.createSheet("Category Detail");
        int[] widths = {7000, 5500, 3500, 1200, 7000, 5500, 3500};
        for (int i = 0; i < widths.length; i++) sheet.setColumnWidth(i, widths[i]);

        int row = 0;
        row = sectionMerge(sheet, xssfWb, row, "Category Breakdown", 7);
        row = periodRow(sheet, xssfWb, row, r);
        row++;

        SXSSFRow hdr = sheet.createRow(row++);
        hdr.setHeightInPoints(20);
        tableHdrCell(hdr, 0, "Inflow Category",  st.tableHdrGrn);
        tableHdrCell(hdr, 1, "Amount",            st.tableHdrGrn);
        tableHdrCell(hdr, 2, "% of Inflows",      st.tableHdrGrn);
        cell(hdr, 3, "", st.plain);
        tableHdrCell(hdr, 4, "Outflow Category",  st.tableHdrRed);
        tableHdrCell(hdr, 5, "Amount",            st.tableHdrRed);
        tableHdrCell(hdr, 6, "% of Outflows",     st.tableHdrRed);

        List<CashFlowLineItemDTO> ins  = r.getInflowByCategory();
        List<CashFlowLineItemDTO> outs = r.getOutflowByCategory();
        int max = Math.max(ins.size(), outs.size());

        for (int i = 0; i < max; i++) {
            SXSSFRow xRow = sheet.createRow(row++);
            xRow.setHeightInPoints(18);
            CellStyle base = (i % 2 == 1) ? st.alt : st.plain;

            if (i < ins.size()) {
                CashFlowLineItemDTO c = ins.get(i);
                cell(xRow, 0, c.getLabel(), base);
                numCell(xRow, 1, c.getAmount(), st.numGreen(xssfWb, base));
                cell(xRow, 2, c.getPercentage() + "%", base);
            } else { cell(xRow, 0, "", base); cell(xRow, 1, "", base); cell(xRow, 2, "", base); }

            cell(xRow, 3, "", st.plain);

            if (i < outs.size()) {
                CashFlowLineItemDTO c = outs.get(i);
                cell(xRow, 4, c.getLabel(), base);
                numCell(xRow, 5, c.getAmount(), st.numRed(xssfWb, base));
                cell(xRow, 6, c.getPercentage() + "%", base);
            } else { cell(xRow, 4, "", base); cell(xRow, 5, "", base); cell(xRow, 6, "", base); }
        }

        SXSSFRow tot = sheet.createRow(row);
        tot.setHeightInPoints(20);
        cell(tot, 0, "Total Inflows",    st.totalLabel); numCell(tot, 1, r.getTotalInflow(),  st.totalNum(xssfWb)); cell(tot, 2, "100%", st.totalLabel);
        cell(tot, 3, "", st.plain);
        cell(tot, 4, "Total Outflows",   st.totalLabel); numCell(tot, 5, r.getTotalOutflow(), st.totalNum(xssfWb)); cell(tot, 6, "100%", st.totalLabel);
    }

    // ================================================================
    // HEADER HELPERS
    // ================================================================

    private int bigTitle(SXSSFSheet sheet, XSSFWorkbook wb, int rowIdx, String text) {
        SXSSFRow row = sheet.createRow(rowIdx++);
        row.setHeightInPoints(30);
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(C_NAVY, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 16);
        f.setColor(new XSSFColor(C_WHITE, null)); s.setFont(f);
        cell(row, 0, text, s); cell(row, 1, "", s); cell(row, 2, "", s);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx-1, rowIdx-1, 0, 2));
        return rowIdx;
    }

    private int sectionMerge(SXSSFSheet sheet, XSSFWorkbook wb, int rowIdx, String text, int cols) {
        SXSSFRow row = sheet.createRow(rowIdx++);
        row.setHeightInPoints(26);
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(C_NAVY, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorders(s, BorderStyle.THIN);
        XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 14);
        f.setColor(new XSSFColor(C_WHITE, null)); s.setFont(f);
        cell(row, 0, text, s);
        for (int c = 1; c < cols; c++) cell(row, c, "", s);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx-1, rowIdx-1, 0, cols-1));
        return rowIdx;
    }

    private int periodRow(SXSSFSheet sheet, XSSFWorkbook wb, int rowIdx, CashFlowReportDTO r) {
        SXSSFRow row = sheet.createRow(rowIdx++);
        row.setHeightInPoints(18);
        String from = r.getDateFrom() != null ? r.getDateFrom().format(DATE_FMT) : "—";
        String to   = r.getDateTo()   != null ? r.getDateTo().format(DATE_FMT)   : "—";
        String text = "Period: " + from + "  →  " + to + "        Generated: " + r.getGeneratedAt();
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(C_LIGHT, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        XSSFFont f = wb.createFont(); f.setItalic(true); s.setFont(f);
        cell(row, 0, text, s); cell(row, 1, "", s); cell(row, 2, "", s);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx-1, rowIdx-1, 0, 2));
        return rowIdx;
    }

    // ================================================================
    // INLINE STYLES
    // ================================================================

    private XSSFCellStyle sectionStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(C_SECTION, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.LEFT);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorders(s, BorderStyle.THIN);
        XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 11);
        f.setColor(new XSSFColor(C_WHITE, null)); s.setFont(f);
        return s;
    }

    private XSSFCellStyle categoryNumStyle(XSSFWorkbook wb, byte[] fg) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setAlignment(HorizontalAlignment.RIGHT);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
        setBorders(s, BorderStyle.THIN);
        XSSFFont f = wb.createFont(); f.setColor(new XSSFColor(fg, null)); s.setFont(f);
        return s;
    }

    private XSSFCellStyle subtotalStyle(XSSFWorkbook wb, byte[] fg, byte[] bg) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(bg, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.RIGHT);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
        setBorders(s, BorderStyle.MEDIUM);
        XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 10);
        f.setColor(new XSSFColor(fg, null)); s.setFont(f);
        return s;
    }

    private XSSFCellStyle netStyle(XSSFWorkbook wb, byte[] fg, byte[] bg) {
        XSSFCellStyle s = subtotalStyle(wb, fg, bg);
        XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 13);
        f.setColor(new XSSFColor(fg, null)); s.setFont(f);
        return s;
    }

    private void tableHdrCell(Row row, int col, String value, CellStyle style) {
        Cell c = row.createCell(col); c.setCellValue(value); c.setCellStyle(style);
    }

    // ================================================================
    // STYLE BUNDLE
    // ================================================================

    private class Styles {
        final XSSFCellStyle plain, alt, tableHdr, tableHdrGrn, tableHdrRed, totalLabel, pct, note;
        private final DataFormat fmt;

        Styles(XSSFWorkbook wb) {
            fmt = wb.createDataFormat();

            plain = wb.createCellStyle();
            plain.setAlignment(HorizontalAlignment.CENTER);
            plain.setVerticalAlignment(VerticalAlignment.CENTER);
            setBorders(plain, BorderStyle.THIN);

            alt = wb.createCellStyle(); alt.cloneStyleFrom(plain);
            alt.setFillForegroundColor(new XSSFColor(C_ALT, null));
            alt.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            tableHdr    = buildHdr(wb, C_NAVY);
            tableHdrGrn = buildHdr(wb, new byte[]{(byte)0x1A, (byte)0x79, (byte)0x31});
            tableHdrRed = buildHdr(wb, new byte[]{(byte)0x96, (byte)0x00, (byte)0x18});

            totalLabel = wb.createCellStyle();
            totalLabel.setFillForegroundColor(new XSSFColor(C_NAVY, null));
            totalLabel.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalLabel.setAlignment(HorizontalAlignment.RIGHT);
            totalLabel.setVerticalAlignment(VerticalAlignment.CENTER);
            setBorders(totalLabel, BorderStyle.MEDIUM);
            XSSFFont tf = wb.createFont(); tf.setBold(true); tf.setFontHeightInPoints((short) 10);
            tf.setColor(new XSSFColor(C_WHITE, null)); totalLabel.setFont(tf);

            pct = wb.createCellStyle(); pct.cloneStyleFrom(plain);
            pct.setAlignment(HorizontalAlignment.CENTER);

            note = wb.createCellStyle();
            note.setAlignment(HorizontalAlignment.LEFT);
            XSSFFont nf = wb.createFont(); nf.setItalic(true); nf.setFontHeightInPoints((short) 8); note.setFont(nf);
        }

        private XSSFCellStyle buildHdr(XSSFWorkbook wb, byte[] bg) {
            XSSFCellStyle s = wb.createCellStyle();
            s.setFillForegroundColor(new XSSFColor(bg, null));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            s.setAlignment(HorizontalAlignment.CENTER);
            s.setVerticalAlignment(VerticalAlignment.CENTER);
            setBorders(s, BorderStyle.THIN);
            XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 10);
            f.setColor(new XSSFColor(C_WHITE, null)); s.setFont(f);
            return s;
        }

        XSSFCellStyle numGreen(XSSFWorkbook wb, CellStyle base) {
            XSSFCellStyle s = wb.createCellStyle(); s.cloneStyleFrom(base);
            s.setAlignment(HorizontalAlignment.RIGHT);
            s.setDataFormat(fmt.getFormat("#,##0.00"));
            XSSFFont f = wb.createFont(); f.setColor(new XSSFColor(C_GRN_FG, null)); s.setFont(f);
            return s;
        }

        XSSFCellStyle numRed(XSSFWorkbook wb, CellStyle base) {
            XSSFCellStyle s = wb.createCellStyle(); s.cloneStyleFrom(base);
            s.setAlignment(HorizontalAlignment.RIGHT);
            s.setDataFormat(fmt.getFormat("#,##0.00"));
            XSSFFont f = wb.createFont(); f.setColor(new XSSFColor(C_RED_FG, null)); s.setFont(f);
            return s;
        }

        XSSFCellStyle totalNum(XSSFWorkbook wb) {
            XSSFCellStyle s = wb.createCellStyle(); s.cloneStyleFrom(totalLabel);
            s.setDataFormat(fmt.getFormat("#,##0.00"));
            return s;
        }
    }

    // ================================================================
    // CELL HELPERS
    // ================================================================

    private void cell(Row row, int col, String value, CellStyle style) {
        Cell c = row.createCell(col); c.setCellValue(value != null ? value : ""); c.setCellStyle(style);
    }

    private void numCell(Row row, int col, BigDecimal value, CellStyle style) {
        Cell c = row.createCell(col); c.setCellValue(value != null ? value.doubleValue() : 0.0); c.setCellStyle(style);
    }

    private void setBorders(CellStyle s, BorderStyle bs) {
        s.setBorderTop(bs); s.setBorderBottom(bs); s.setBorderLeft(bs); s.setBorderRight(bs);
    }

    private static byte[] hex(String h) {
        byte[] b = new byte[h.length() / 2];
        for (int i = 0; i < b.length; i++)
            b[i] = (byte) ((Character.digit(h.charAt(i*2), 16) << 4) + Character.digit(h.charAt(i*2+1), 16));
        return b;
    }
}