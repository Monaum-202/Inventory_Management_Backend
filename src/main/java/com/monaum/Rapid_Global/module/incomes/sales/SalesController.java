package com.monaum.Rapid_Global.module.incomes.sales;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestApiController
@RequestMapping("/api/sales")
public class SalesController {

    @Autowired private SalesService service;
    @Autowired private SalesInvoiceReportService reportService;

    @PostMapping
    public ResponseEntity<BaseApiResponseDTO<?>> create(
            @Valid @RequestBody SalesReqDTO dto
    ){
        return   service.create(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseApiResponseDTO<?>> update(
            @PathVariable Long id,
            @Valid @RequestBody SalesReqUpdateDTO dto
    ){
        return   service.update(id, dto);
    }

    @GetMapping
    public ResponseEntity<BaseApiResponseDTO<?>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("invoiceNo").descending());
        return service.getAll(search, pageable);
    }


    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) {

        SalesResDto sales = service.getById(id);

        SalesInvoiceJasperDto dto = new SalesInvoiceJasperDto(
                sales.getInvoiceNo(),
                sales.getCustomerName(),
                sales.getPhone(),
                sales.getAddress(),
                sales.getSellDate().toString(),
                sales.getDeliveryDate().toString(),
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
