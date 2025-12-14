package com.monaum.Rapid_Global.module.incomes.sales.pdf;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendInvoiceEmail(String toEmail, String customerName, String invoiceNo, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Invoice #" + invoiceNo + " - Thank You for Your Business");

            String emailBody = buildEmailBody(customerName, invoiceNo);
            helper.setText(emailBody, true);

            helper.addAttachment("Invoice_" + invoiceNo + ".pdf", new ByteArrayResource(pdfBytes));

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send invoice email: " + e.getMessage(), e);
        }
    }

    private String buildEmailBody(String customerName, String invoiceNo) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #000; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background: #f9f9f9; }
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>Invoice Notification</h2>
                    </div>
                    <div class="content">
                        <p>Dear %s,</p>
                        <p>Thank you for your business! Please find your invoice attached.</p>
                        <p><strong>Invoice Number:</strong> %s</p>
                        <p>If you have any questions, please contact us.</p>
                        <p>Best regards,<br>Your Company Name</p>
                    </div>
                    <div class="footer">
                        <p>This is an automated email. Please do not reply.</p>
                        <p>&copy; 2024 Your Company. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(customerName, invoiceNo);
    }
}