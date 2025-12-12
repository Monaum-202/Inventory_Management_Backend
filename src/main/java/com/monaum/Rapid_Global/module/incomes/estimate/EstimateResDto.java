package com.monaum.Rapid_Global.module.incomes.estimate;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 12-Dec-25
 */

@Data
public class EstimateResDto {

    private Long id;
    private String estimateNo;
    private String customerName;
    private String phone;
    private String email;
    private String address;
    private String companyName;
    private LocalDate estimateDate;
    private LocalDate expiryDate;
    private String notes;

    private Double subTotal;
    private Double discount;
    private Double vat;
    private Double totalAmount;
    private String status;

    private Boolean convertedToSale;
    private Long saleId;

    private List<EstimateItemResDto> items;
}