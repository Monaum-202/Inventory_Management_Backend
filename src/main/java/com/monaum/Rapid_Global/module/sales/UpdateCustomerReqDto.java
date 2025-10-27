package com.monaum.Rapid_Global.module.sales;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerReqDto {
    @NotNull(message = "Company ID is required.")
    private Long id;

    @NotBlank(message = "Company name required.")
    private String name;

    @NotNull(message = "Phone is required.")
    private String phone;

    private Long altPhone;

    private String email;

    private String address;

    private String businessAddress;

    private BigDecimal totalTransaction;

    private Long companyId;
}
