package com.monaum.Rapid_Global.module.incomes.sales;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sa")
@RequiredArgsConstructor
public class SalesInvoiceController {

    private final SalesService salesService;
    private final SalesInvoiceReportService reportService;

    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) {

        SalesResDto sales = salesService.getById(id);

        SalesInvoiceJasperDto dto = new SalesInvoiceJasperDto(
                sales.getInvoiceNo(),
                sales.getCustomerName(),
                sales.getPhone(),
                sales.getAddress(),
                sales.getSellDate().toString(),
                sales.getTotalAmount(),
                sales.getPaidAmount(),
                sales.getDueAmount(),
                convertNumberToWords(sales.getTotalAmount()),
                sales.getItems()
        );

        byte[] pdf = reportService.generateInvoice(dto);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=invoice_" + sales.getInvoiceNo() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // simple words converter
    private String convertNumberToWords(Double amount) {
        return amount.intValue() + " Taka Only";
    }
}
