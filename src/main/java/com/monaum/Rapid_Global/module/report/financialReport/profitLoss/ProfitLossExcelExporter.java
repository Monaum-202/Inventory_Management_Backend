package com.monaum.Rapid_Global.module.report.financialReport.profitLoss;

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

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * Produces a 3-sheet Excel workbook:
 *   Sheet 1 — P&L Statement  (the classic two-column statement)
 *   Sheet 2 — Monthly Trend  (income / expense / net per month)
 *   Sheet 3 — Category Detail (income categories + expense categories side by side)
 */
@Component
public class ProfitLossExcelExporter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    // Colours
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
    private static final byte[] C_DARK    = hex("2E2E2E");
    private static final byte[] C_SECTION = hex("344F6E");

    // ----------------------------------------------------------------

    public void export(ProfitLossReportDTO report, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"PL_Report_" + System.currentTimeMillis() + ".xlsx\"");

        XSSFWorkbook xssfWb = new XSSFWorkbook();
        try (SXSSFWorkbook wb = new SXSSFWorkbook(xssfWb, 200)) {
            wb.setCompressTempFiles(true);
            Styles st = new Styles(xssfWb);

            writePLStatement(wb, xssfWb, st, report);
            writeMonthlySheet(wb, xssfWb, st, report);
            writeCategorySheet(wb, xssfWb, st, report);

            wb.write(response.getOutputStream());
        }
    }

    // ================================================================
    // SHEET 1 — P&L STATEMENT
    // ================================================================

    private void writePLStatement(SXSSFWorkbook wb, XSSFWorkbook xssfWb,
                                  Styles st, ProfitLossReportDTO r) {
        SXSSFSheet sheet = wb.createSheet("P&L Statement");
        sheet.setColumnWidth(0, 9000);   // Label
        sheet.setColumnWidth(1, 5500);   // Amount
        sheet.setColumnWidth(2, 3500);   // %

        int row = 0;

        // ---- Company / title header ----
        row = bigTitle(sheet, st, xssfWb, row, "RAPID GLOBAL", "PROFIT & LOSS STATEMENT");
        row = periodRow(sheet, st, xssfWb, row, r);
        row++;

        // ---- Render each line item ----
        for (ProfitLossLineItemDTO item : r.getLineItems()) {
            String lt = item.getLineType();

            if (ProfitLossReportService.LT_SECTION_HEADER.equals(lt)) {
                SXSSFRow xRow = sheet.createRow(row++);
                xRow.setHeightInPoints(22);
                XSSFCellStyle sectionSt = sectionHeaderStyle(xssfWb);
                cell(xRow, 0, item.getLabel(), sectionSt);
                cell(xRow, 1, "",              sectionSt);
                cell(xRow, 2, "",              sectionSt);
                sheet.addMergedRegion(new CellRangeAddress(row-1, row-1, 0, 2));

            } else if (ProfitLossReportService.LT_CATEGORY.equals(lt)) {
                SXSSFRow xRow = sheet.createRow(row++);
                xRow.setHeightInPoints(18);
                Boolean pos = item.getPositive();
                XSSFCellStyle numSt = categoryAmountStyle(xssfWb,
                        Boolean.TRUE.equals(pos) ? C_GRN_FG : C_RED_FG);
                cell   (xRow, 0, "    " + item.getLabel(), st.plain);
                numCell(xRow, 1, item.getAmount(), numSt);
                cell   (xRow, 2,
                        item.getPercentage() != null ? item.getPercentage() + "%" : "",
                        st.pctStyle);

            } else if (ProfitLossReportService.LT_SUBTOTAL.equals(lt)) {
                SXSSFRow xRow = sheet.createRow(row++);
                xRow.setHeightInPoints(20);
                boolean pos = Boolean.TRUE.equals(item.getPositive());
                XSSFCellStyle subtSt = subtotalStyle(xssfWb,
                        pos ? C_GRN_FG : C_RED_FG,
                        pos ? C_GRN_BG : C_RED_BG);
                cell   (xRow, 0, item.getLabel(), subtSt);
                numCell(xRow, 1, item.getAmount(), subtSt);
                cell   (xRow, 2, "", subtSt);

            } else if (ProfitLossReportService.LT_SPACER.equals(lt)) {
                sheet.createRow(row++).setHeightInPoints(8);

            } else if (ProfitLossReportService.LT_NET.equals(lt)) {
                SXSSFRow xRow = sheet.createRow(row++);
                xRow.setHeightInPoints(26);
                boolean isProfit = Boolean.TRUE.equals(item.getPositive());
                XSSFCellStyle netSt = netStyle(xssfWb,
                        isProfit ? C_GRN_FG : C_RED_FG,
                        isProfit ? C_GRN_BG : C_RED_BG);
                cell   (xRow, 0, item.getLabel(), netSt);
                numCell(xRow, 1, item.getAmount(), netSt);
                BigDecimal margin = r.getNetMarginPct();
                cell   (xRow, 2,
                        margin != null ? margin + "%" : "",
                        netSt);
            }
        }

        // ---- Note ----
        row++;
        SXSSFRow note = sheet.createRow(row);
        note.setHeightInPoints(14);
        cell(note, 0, "* Includes APPROVED transactions only.", st.noteStyle);
        sheet.addMergedRegion(new CellRangeAddress(row, row, 0, 2));
    }

    // ================================================================
    // SHEET 2 — MONTHLY TREND
    // ================================================================

    private void writeMonthlySheet(SXSSFWorkbook wb, XSSFWorkbook xssfWb,
                                   Styles st, ProfitLossReportDTO r) {
        SXSSFSheet sheet = wb.createSheet("Monthly Trend");
        sheet.setColumnWidth(0, 4500);  // Month
        sheet.setColumnWidth(1, 5500);  // Income
        sheet.setColumnWidth(2, 5500);  // Expense
        sheet.setColumnWidth(3, 5500);  // Net

        int row = 0;
        row = sectionMerge(sheet, st, xssfWb, row, "Monthly Income vs Expense", 4);
        row = periodRow(sheet, st, xssfWb, row, r);
        row++;

        // Header
        SXSSFRow hdr = sheet.createRow(row++);
        hdr.setHeightInPoints(20);
        tableHeaderCell(hdr, 0, "Month",         st.tableHdr);
        tableHeaderCell(hdr, 1, "Income",         st.tableHdr);
        tableHeaderCell(hdr, 2, "Expense",        st.tableHdr);
        tableHeaderCell(hdr, 3, "Net Profit/(Loss)", st.tableHdr);

        boolean alt = false;
        BigDecimal sumInc = BigDecimal.ZERO, sumExp = BigDecimal.ZERO, sumNet = BigDecimal.ZERO;

        for (MonthlyBreakdownDTO m : r.getMonthlyBreakdown()) {
            SXSSFRow xRow = sheet.createRow(row++);
            xRow.setHeightInPoints(18);
            CellStyle base = alt ? st.alt : st.plain;

            cell   (xRow, 0, m.getMonthLabel(),  base);
            numCell(xRow, 1, m.getTotalIncome(),  st.numGreen(xssfWb, base));
            numCell(xRow, 2, m.getTotalExpense(), st.numRed(xssfWb,   base));

            boolean profit = m.getNetProfit().compareTo(BigDecimal.ZERO) >= 0;
            numCell(xRow, 3, m.getNetProfit().abs(),
                    profit ? st.numGreen(xssfWb, base) : st.numRed(xssfWb, base));

            sumInc = sumInc.add(m.getTotalIncome());
            sumExp = sumExp.add(m.getTotalExpense());
            sumNet = sumNet.add(m.getNetProfit());
            alt    = !alt;
        }

        // Totals
        SXSSFRow tot = sheet.createRow(row);
        tot.setHeightInPoints(20);
        cell   (tot, 0, "TOTALS", st.totalLabel);
        numCell(tot, 1, sumInc,   st.totalNum(xssfWb));
        numCell(tot, 2, sumExp,   st.totalNum(xssfWb));
        numCell(tot, 3, sumNet.abs(), st.totalNum(xssfWb));
    }

    // ================================================================
    // SHEET 3 — CATEGORY DETAIL
    // ================================================================

    private void writeCategorySheet(SXSSFWorkbook wb, XSSFWorkbook xssfWb,
                                    Styles st, ProfitLossReportDTO r) {
        SXSSFSheet sheet = wb.createSheet("Category Breakdown");
        sheet.setColumnWidth(0, 7000); sheet.setColumnWidth(1, 5500); sheet.setColumnWidth(2, 3500);
        sheet.setColumnWidth(3, 1200);
        sheet.setColumnWidth(4, 7000); sheet.setColumnWidth(5, 5500); sheet.setColumnWidth(6, 3500);

        int row = 0;
        row = sectionMerge(sheet, st, xssfWb, row, "Category Breakdown", 7);
        row = periodRow(sheet, st, xssfWb, row, r);
        row++;

        // Side-by-side headers
        SXSSFRow hdr = sheet.createRow(row++);
        hdr.setHeightInPoints(20);
        tableHeaderCell(hdr, 0, "Income Category",  st.tableHdrGrn);
        tableHeaderCell(hdr, 1, "Amount",            st.tableHdrGrn);
        tableHeaderCell(hdr, 2, "% of Income",       st.tableHdrGrn);
        cell(hdr, 3, "", st.plain);
        tableHeaderCell(hdr, 4, "Expense Category",  st.tableHdrRed);
        tableHeaderCell(hdr, 5, "Amount",            st.tableHdrRed);
        tableHeaderCell(hdr, 6, "% of Expenses",     st.tableHdrRed);

        int maxRows = Math.max(r.getIncomeByCategory().size(), r.getExpenseByCategory().size());
        for (int i = 0; i < maxRows; i++) {
            SXSSFRow xRow = sheet.createRow(row++);
            xRow.setHeightInPoints(18);
            boolean alt = (i % 2 == 1);
            CellStyle base = alt ? st.alt : st.plain;

            if (i < r.getIncomeByCategory().size()) {
                CategoryBreakdownDTO c = r.getIncomeByCategory().get(i);
                cell   (xRow, 0, c.getCategoryName(), base);
                numCell(xRow, 1, c.getAmount(),       st.numGreen(xssfWb, base));
                cell   (xRow, 2, c.getPercentage() + "%", base);
            } else {
                cell(xRow, 0, "", base); cell(xRow, 1, "", base); cell(xRow, 2, "", base);
            }

            cell(xRow, 3, "", st.plain);

            if (i < r.getExpenseByCategory().size()) {
                CategoryBreakdownDTO c = r.getExpenseByCategory().get(i);
                cell   (xRow, 4, c.getCategoryName(), base);
                numCell(xRow, 5, c.getAmount(),       st.numRed(xssfWb, base));
                cell   (xRow, 6, c.getPercentage() + "%", base);
            } else {
                cell(xRow, 4, "", base); cell(xRow, 5, "", base); cell(xRow, 6, "", base);
            }
        }

        // Subtotals
        SXSSFRow tot = sheet.createRow(row);
        tot.setHeightInPoints(20);
        cell   (tot, 0, "Total Income",   st.totalLabel);
        numCell(tot, 1, r.getTotalIncome(),  st.totalNum(xssfWb));
        cell   (tot, 2, "100%",              st.totalLabel);
        cell   (tot, 3, "",                  st.plain);
        cell   (tot, 4, "Total Expenses", st.totalLabel);
        numCell(tot, 5, r.getTotalExpense(), st.totalNum(xssfWb));
        cell   (tot, 6, "100%",              st.totalLabel);
    }

    // ================================================================
    // HEADER HELPERS
    // ================================================================

    private int bigTitle(SXSSFSheet sheet, Styles st, XSSFWorkbook xssfWb,
                         int rowIdx, String company, String title) {
        // Company name row
        SXSSFRow r1 = sheet.createRow(rowIdx++);
        r1.setHeightInPoints(30);
        XSSFCellStyle cs = xssfWb.createCellStyle();
        cs.setFillForegroundColor(new XSSFColor(C_NAVY, null));
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorders(cs, BorderStyle.NONE);
        XSSFFont f1 = xssfWb.createFont(); f1.setBold(true); f1.setFontHeightInPoints((short)18);
        f1.setColor(new XSSFColor(C_WHITE, null)); cs.setFont(f1);
        cell(r1, 0, company + "  —  " + title, cs);
        cell(r1, 1, "", cs); cell(r1, 2, "", cs);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx-1, rowIdx-1, 0, 2));
        return rowIdx;
    }

    private int sectionMerge(SXSSFSheet sheet, Styles st, XSSFWorkbook xssfWb,
                             int rowIdx, String text, int cols) {
        SXSSFRow row = sheet.createRow(rowIdx++);
        row.setHeightInPoints(26);
        XSSFCellStyle cs = xssfWb.createCellStyle();
        cs.setFillForegroundColor(new XSSFColor(C_NAVY, null));
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorders(cs, BorderStyle.THIN);
        XSSFFont f = xssfWb.createFont(); f.setBold(true); f.setFontHeightInPoints((short)14);
        f.setColor(new XSSFColor(C_WHITE, null)); cs.setFont(f);
        cell(row, 0, text, cs);
        for (int c = 1; c < cols; c++) cell(row, c, "", cs);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx-1, rowIdx-1, 0, cols-1));
        return rowIdx;
    }

    private int periodRow(SXSSFSheet sheet, Styles st, XSSFWorkbook xssfWb,
                          int rowIdx, ProfitLossReportDTO r) {
        SXSSFRow row = sheet.createRow(rowIdx++);
        row.setHeightInPoints(18);
        String from = r.getDateFrom() != null ? r.getDateFrom().format(DATE_FMT) : "—";
        String to   = r.getDateTo()   != null ? r.getDateTo().format(DATE_FMT)   : "—";
        String text = "Period: " + from + "  →  " + to
                + "        Generated: " + r.getGeneratedAt();
        XSSFCellStyle cs = xssfWb.createCellStyle();
        cs.setFillForegroundColor(new XSSFColor(C_LIGHT, null));
        cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cs.setAlignment(HorizontalAlignment.CENTER);
        XSSFFont f = xssfWb.createFont(); f.setItalic(true); cs.setFont(f);
        cell(row, 0, text, cs); cell(row, 1, "", cs); cell(row, 2, "", cs);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx-1, rowIdx-1, 0, 2));
        return rowIdx;
    }

    // ================================================================
    // INLINE STYLES (created on demand, not cached — keeps StyleBundle lean)
    // ================================================================

    private XSSFCellStyle sectionHeaderStyle(XSSFWorkbook wb) {
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

    private XSSFCellStyle categoryAmountStyle(XSSFWorkbook wb, byte[] fgColour) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setAlignment(HorizontalAlignment.RIGHT);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
        setBorders(s, BorderStyle.THIN);
        XSSFFont f = wb.createFont(); f.setColor(new XSSFColor(fgColour, null)); s.setFont(f);
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

    // ================================================================
    // CACHED STYLE BUNDLE
    // ================================================================

    private class Styles {
        final XSSFCellStyle plain, alt, tableHdr, tableHdrGrn, tableHdrRed,
                totalLabel, pctStyle, noteStyle;
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

            tableHdr = buildHeaderStyle(wb, C_NAVY);
            tableHdrGrn = buildHeaderStyle(wb, C_GRN_FG);
            tableHdrRed = buildHeaderStyle(wb, C_RED_FG);

            totalLabel = wb.createCellStyle();
            totalLabel.setFillForegroundColor(new XSSFColor(C_NAVY, null));
            totalLabel.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalLabel.setAlignment(HorizontalAlignment.RIGHT);
            totalLabel.setVerticalAlignment(VerticalAlignment.CENTER);
            setBorders(totalLabel, BorderStyle.MEDIUM);
            XSSFFont tf = wb.createFont(); tf.setBold(true); tf.setFontHeightInPoints((short)10);
            tf.setColor(new XSSFColor(C_WHITE, null)); totalLabel.setFont(tf);

            pctStyle = wb.createCellStyle(); pctStyle.cloneStyleFrom(plain);
            pctStyle.setAlignment(HorizontalAlignment.CENTER);
            XSSFFont pf = wb.createFont(); pf.setColor(new XSSFColor(C_DARK, null));
            pf.setFontHeightInPoints((short)9); pctStyle.setFont(pf);

            noteStyle = wb.createCellStyle();
            noteStyle.setAlignment(HorizontalAlignment.LEFT);
            XSSFFont nf = wb.createFont(); nf.setItalic(true); nf.setFontHeightInPoints((short)8);
            nf.setColor(new XSSFColor(C_DARK, null)); noteStyle.setFont(nf);
        }

        private XSSFCellStyle buildHeaderStyle(XSSFWorkbook wb, byte[] bg) {
            XSSFCellStyle s = wb.createCellStyle();
            s.setFillForegroundColor(new XSSFColor(bg, null));
            s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            s.setAlignment(HorizontalAlignment.CENTER);
            s.setVerticalAlignment(VerticalAlignment.CENTER);
            setBorders(s, BorderStyle.THIN);
            XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short)10);
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
        Cell c = row.createCell(col);
        c.setCellValue(value != null ? value : "");
        c.setCellStyle(style);
    }

    private void numCell(Row row, int col, BigDecimal value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value != null ? value.doubleValue() : 0.0);
        c.setCellStyle(style);
    }

    private void tableHeaderCell(Row row, int col, String value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value);
        c.setCellStyle(style);
    }

    private void setBorders(CellStyle s, BorderStyle bs) {
        s.setBorderTop(bs); s.setBorderBottom(bs);
        s.setBorderLeft(bs); s.setBorderRight(bs);
    }

    private static byte[] hex(String h) {
        byte[] b = new byte[h.length() / 2];
        for (int i = 0; i < b.length; i++)
            b[i] = (byte) ((Character.digit(h.charAt(i * 2), 16) << 4)
                    + Character.digit(h.charAt(i * 2 + 1), 16));
        return b;
    }
}