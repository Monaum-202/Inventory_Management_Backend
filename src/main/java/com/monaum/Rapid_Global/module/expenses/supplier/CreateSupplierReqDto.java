package com.monaum.Rapid_Global.module.expenses.supplier;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSupplierReqDto {

    @NotBlank(message = "Company name required.")
    private String name;

    @NotNull(message = "Phone is required.")
    private String phone;

    private Long altPhone;

    private String email;

    private String address;

    private String businessAddress;

    private BigDecimal totalTransaction;

    private BigDecimal due;

    private Long companyId;
}
