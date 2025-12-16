package com.monaum.Rapid_Global.module.expenses.purchase;

import com.monaum.Rapid_Global.module.expenses.purchaseItem.PurchaseItemReqDto;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PurchaseReqUpdateDTO {

    @NotBlank(message = "Supplier name is required")
    @Size(max = 100, message = "Supplier name cannot exceed 100 characters")
    private String supplierName;

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

    @NotNull(message = "Sell date is required")
    private LocalDate sellDate;

    @NotNull(message = "Delivery date is required")
    private LocalDate deliveryDate;

    private String notes;

    @NotBlank(message = "Status is required")
    private String status;

    @NotEmpty(message = "Purchase items cannot be empty")
    private List<PurchaseItemReqDto> items;

}