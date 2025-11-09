package com.monaum.Rapid_Global.module.incomes.customer;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerReqDto {

    @NotBlank(message = "Company name is required.")
    private String name;

    @NotBlank(message = "Phone is required.")
    private String phone;

    private Long altPhone;

    private String email;

    private String address;

    private String businessAddress;
}
