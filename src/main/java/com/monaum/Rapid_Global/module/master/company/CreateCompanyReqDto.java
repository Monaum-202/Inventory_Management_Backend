package com.monaum.Rapid_Global.module.master.company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCompanyReqDto {

    @NotBlank(message = "Company name required.")
    private String name_abcd;

    private String address;

    @NotBlank(message = "Phone is required.")
    private String phone;

    private String email;

    private Boolean status;

}
