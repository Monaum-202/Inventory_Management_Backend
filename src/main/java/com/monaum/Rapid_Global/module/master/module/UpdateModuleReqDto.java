package com.monaum.Rapid_Global.module.master.module;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateModuleReqDto {

    @NotNull(message = "Module ID is required.")
    private Long id;

    @NotBlank(message = "Module name is required.")
    private String name;

    @NotBlank(message = "Route is required.")
    private String route;

    private Integer sqnce;

    @NotNull(message = "Company ID is required.")
    private Long companyId;
}
