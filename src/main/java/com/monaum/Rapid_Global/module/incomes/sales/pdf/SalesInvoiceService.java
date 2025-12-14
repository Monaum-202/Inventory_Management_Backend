package com.monaum.Rapid_Global.module.incomes.sales.pdf;

import com.monaum.Rapid_Global.model.NumberToWordsUtil;
import com.monaum.Rapid_Global.module.incomes.sales.SalesInvoiceJasperDto;
import com.monaum.Rapid_Global.module.incomes.sales.SalesResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesInvoiceService {

    private final SalesInvoiceReportService reportService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    /**
     * Generate PDF bytes from a SalesResDto
     */
    public byte[] generateInvoicePdf(SalesResDto sales) {
        SalesInvoiceJasperDto dto = mapToDto(sales);
        return reportService.generateInvoice(dto);
    }

    /**
     * Map SalesResDto to SalesInvoiceJasperDto
     */
    private SalesInvoiceJasperDto mapToDto(SalesResDto sales) {
        SalesInvoiceJasperDto dto = new SalesInvoiceJasperDto();

        dto.setInvoiceNo(sales.getInvoiceNo());
        dto.setCustomerName(sales.getCustomerName());
        dto.setPhone(sales.getPhone());
        dto.setAddress(sales.getAddress());
        dto.setSellDate(sales.getSellDate() != null ? sales.getSellDate().format(DATE_FORMATTER) : "");
        dto.setDeliveryDate(sales.getDeliveryDate() != null ? sales.getDeliveryDate().format(DATE_FORMATTER) : "");

        dto.setSubTotal(sales.getSubTotal());
        dto.setDiscount(sales.getDiscount() != null ? sales.getDiscount() : 0.0);
        dto.setVat(sales.getVat() != null ? sales.getVat() : 0.0);
        dto.setTotalAmount(sales.getTotalAmount());
        dto.setPaidAmount(sales.getPaidAmount());
        dto.setDueAmount(sales.getDueAmount());
        dto.setAmountInWords(NumberToWordsUtil.convert(sales.getTotalAmount()));

        List<SalesInvoiceJasperDto.SalesInvoiceItemDto> items = new ArrayList<>();
        if (sales.getItems() != null) {
            for (var item : sales.getItems()) {
                items.add(new SalesInvoiceJasperDto.SalesInvoiceItemDto(
                        item.getItemName(),
                        item.getUnitName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice()
                ));
            }
        }
        dto.setItems(items);

        return dto;
    }
}
