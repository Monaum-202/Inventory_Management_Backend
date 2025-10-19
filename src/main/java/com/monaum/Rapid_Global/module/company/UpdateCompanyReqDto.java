package com.monaum.Rapid_Global.module.company;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyReqDto {

    @NotNull(message = "Company ID is required.")
    private Long id;

    @NotNull(message = "Source ID is required.")
//    @ExistsInDatabase(entity = Source.class, message = "Source not found")
    private Long source;

    @NotNull(message = "Wallet ID is required.")
//    @ExistsInDatabase(entity = Wallet.class, message = "Wallet not found")
    private Long wallet;

    @NotNull(message = "Amount is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0.")
    private Double amount;

    @NotBlank(message = "Currency is required.")
    @Size(max = 10, message = "Currency code must be at most 10 characters.")
    private String currency;

    @Size(max = 255, message = "Description can't exceed 255 characters.")
    private String description;

    private LocalDate date; // Consider using LocalDate for better date validation
}
