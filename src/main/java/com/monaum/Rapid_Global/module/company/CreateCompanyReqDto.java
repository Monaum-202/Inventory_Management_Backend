package com.monaum.Rapid_Global.module.company;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCompanyReqDto {

    @NotBlank(message = "Company type required.")
    private String transactionType;

    @NotNull(message = "Source required.")
    private Long source;

    @NotNull(message = "Wallet required.")
    private Long wallet;

    @NotNull(message = "Amount is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0.")
    private Double amount;

    @NotBlank(message = "Currency required.")
    private String currency;

    @NotBlank(message = "Description required.")
    private String description;

    private LocalDate date;

}
