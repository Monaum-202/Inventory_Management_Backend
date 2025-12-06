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
            InputStream reportStream =
                    this.getClass().getResourceAsStream("/reports/sales_invoice.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // PARAMETERS
            Map<String, Object> params = new HashMap<>();
            params.put("slNo", dto.getInvoiceNo());
            params.put("customerName", dto.getCustomerName());
            params.put("phone" , dto.getPhone());
            params.put("customerAddress", dto.getAddress());
            params.put("date", dto.getSellDate());
            params.put("deliveryDate", dto.getDeliveryDate());
            params.put("totalAmount", dto.getTotalAmount());
            params.put("advance", dto.getPaidAmount());
            params.put("dueAmount", dto.getDueAmount());
            params.put("amountInWords", dto.getAmountInWords());

            // ITEMS → Table DataSource (this becomes the main data source)
            JRBeanCollectionDataSource itemsDs =
                    new JRBeanCollectionDataSource(dto.getItems());

            // Fill report with items as data source
            JasperPrint print = JasperFillManager.fillReport(
                    jasperReport,
                    params,
                    itemsDs // This provides the detail rows
            );

            return JasperExportManager.exportReportToPdf(print);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating invoice PDF: " + e.getMessage());
        }
    }
}
