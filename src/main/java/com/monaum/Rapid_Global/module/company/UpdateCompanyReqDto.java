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

    @NotBlank(message = "Company name required.")
    private String name;

    private Long address;

    @NotNull(message = "Phone is required.")
    private String phone;

    private String email;

    private Boolean status;
}
