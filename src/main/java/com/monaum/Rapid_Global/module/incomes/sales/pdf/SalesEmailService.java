package com.monaum.Rapid_Global.module.incomes.sales.pdf;

import com.monaum.Rapid_Global.module.incomes.sales.SalesResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SalesEmailService {

    private final EmailService emailService;
    private final SalesInvoiceService invoiceService;

    public void sendInvoiceEmail(SalesResDto sales, String email) {
        byte[] pdf = invoiceService.generateInvoicePdf(sales);
        emailService.sendInvoiceEmail(email, sales.getCustomerName(), sales.getInvoiceNo(), pdf);
    }
}