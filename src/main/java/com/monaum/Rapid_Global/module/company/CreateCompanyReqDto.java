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

    @NotBlank(message = "Company name required.")
    private String name;

    private Long address;

    @NotNull(message = "Phone is required.")
    private String phone;

    private String email;

    private Boolean status;

}
