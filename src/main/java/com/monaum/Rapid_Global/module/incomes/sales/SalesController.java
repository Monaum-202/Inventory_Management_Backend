package com.monaum.Rapid_Global.module.incomes.sales;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.enums.OrderStatus;
import com.monaum.Rapid_Global.model.NumberToWordsUtil;
import com.monaum.Rapid_Global.module.incomes.sales.pdf.SalesEmailService;
import com.monaum.Rapid_Global.module.incomes.sales.pdf.SalesInvoiceReportService;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

@RestApiController
@RequestMapping("/api/sales")
public class SalesController {

    @Autowired private SalesService service;
    @Autowired private SalesInvoiceReportService reportService;
    @Autowired private SalesEmailService emailService;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy");

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

    @PutMapping("/{id}/approve")
    public ResponseEntity<BaseApiResponseDTO<?>> approve(@PathVariable Long id) {
        return service.updateStatus(id, OrderStatus.COMPLETED,null);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BaseApiResponseDTO<?>> cancel(
            @PathVariable Long id,
            @RequestBody String reason
    ) {
        return service.updateStatus(id, OrderStatus.CANCELLED, reason);
    }

    @PostMapping("/{id}/invoice/email")
    public ResponseEntity<BaseApiResponseDTO<?>> emailInvoice(
            @PathVariable Long id,
            @RequestParam String email) {

        SalesResDto sales = service.getById(id);
        emailService.sendInvoiceEmail(sales, email);

        BaseApiResponseDTO<String> response = new BaseApiResponseDTO<>();
        response.setSuccess(true);
        response.setMessage("Invoice emailed successfully");
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) {

        SalesResDto sales = service.getById(id);

        String amountInWords = NumberToWordsUtil.convert(sales.getTotalAmount());

        SalesInvoiceJasperDto invoiceDto = new SalesInvoiceJasperDto();
        invoiceDto.setInvoiceNo(sales.getInvoiceNo());
        invoiceDto.setCustomerName(sales.getCustomerName());
        invoiceDto.setPhone(sales.getPhone());
        invoiceDto.setAddress(sales.getAddress());
        invoiceDto.setSellDate(
                sales.getSellDate() != null ? sales.getSellDate().format(DATE_FORMATTER) : ""
        );
        invoiceDto.setDeliveryDate(
                sales.getDeliveryDate() != null ? sales.getDeliveryDate().format(DATE_FORMATTER) : ""
        );

        invoiceDto.setSubTotal(sales.getSubTotal());
        invoiceDto.setDiscount(sales.getDiscount());
        invoiceDto.setVat(sales.getVat());
        invoiceDto.setTotalAmount(sales.getTotalAmount());
        invoiceDto.setPaidAmount(sales.getPaidAmount());
        invoiceDto.setDueAmount(sales.getDueAmount());
        invoiceDto.setAmountInWords(amountInWords);

        invoiceDto.setItems(
                sales.getItems().stream()
                        .map(item -> new SalesInvoiceJasperDto.SalesInvoiceItemDto(
                                item.getItemName(),
                                item.getUnitName(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getTotalPrice()
                        ))
                        .toList()
        );

        byte[] pdfBytes = reportService.generateInvoice(invoiceDto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // ✅ INLINE + correct filename
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"" + sales.getInvoiceNo() + ".pdf\"");

        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }


}
