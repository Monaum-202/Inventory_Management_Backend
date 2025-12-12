package com.monaum.Rapid_Global.module.incomes.estimate;


import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 12-Dec-25
 */

@Data
public class EstimateReqDTO {

    @NotBlank(message = "Customer name is required")
    @Size(max = 100, message = "Customer name cannot exceed 100 characters")
    private String customerName;

    @NotBlank(message = "Phone is required")
    @Size(max = 20, message = "Phone cannot exceed 20 characters")
    private String phone;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Size(max = 200, message = "Address cannot exceed 200 characters")
    private String address;

    @Size(max = 200, message = "Company name cannot exceed 200 characters")
    private String companyName;

    @NotNull(message = "Estimate date is required")
    private LocalDate estimateDate;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    private String notes;

    @PositiveOrZero(message = "Discount must be positive or zero")
    private Double discount;

    @PositiveOrZero(message = "VAT must be positive or zero")
    private Double vat;

    @NotBlank(message = "Status is required")
    private String status;

    @NotEmpty(message = "Estimate items cannot be empty")
    private List<EstimateItemReqDto> items;
}