package com.monaum.Rapid_Global.module.incomes.sales;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SalesInvoiceReportService {

    public byte[] generateInvoice(SalesInvoiceJasperDto dto) {
        try {
            // Load the JRXML template
            InputStream reportStream = this.getClass()
                    .getResourceAsStream("/reports/sales_invoice.jrxml");

            if (reportStream == null) {
                throw new RuntimeException("Report template not found at /reports/sales_invoice.jrxml");
            }

            // Compile the report
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // Prepare parameters map
            Map<String, Object> params = new HashMap<>();

            // Invoice header parameters
            params.put("slNo", dto.getInvoiceNo());
            params.put("customerName", dto.getCustomerName());
            params.put("phone", dto.getPhone());
            params.put("customerAddress", dto.getAddress());
            params.put("date", dto.getSellDate());
            params.put("deliveryDate", dto.getDeliveryDate());

            // Financial parameters
            params.put("subTotal", dto.getSubTotal());
            params.put("discount", dto.getDiscount() != null ? dto.getDiscount() : 0.0);
            params.put("vat", dto.getVat() != null ? dto.getVat() : 0.0);
            params.put("totalAmount", dto.getTotalAmount());
            params.put("paidAmount", dto.getPaidAmount());
            params.put("dueAmount", dto.getDueAmount());
            params.put("amountInWords", dto.getAmountInWords());

            // Create data source from items list
            JRBeanCollectionDataSource itemsDataSource =
                    new JRBeanCollectionDataSource(dto.getItems());

            // Fill the report
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    params,
                    itemsDataSource
            );

            // Export to PDF
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (JRException e) {
            throw new RuntimeException("Error generating invoice PDF: " + e.getMessage(), e);
        }
    }
}
