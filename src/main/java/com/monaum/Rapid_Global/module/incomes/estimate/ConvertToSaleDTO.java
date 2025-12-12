package com.monaum.Rapid_Global.module.incomes.estimate;

import lombok.Data;

import java.time.LocalDate;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 12-Dec-25
 */


@Data
public class ConvertToSaleDTO {

    private LocalDate sellDate;
    private LocalDate deliveryDate;
    private String notes;
}