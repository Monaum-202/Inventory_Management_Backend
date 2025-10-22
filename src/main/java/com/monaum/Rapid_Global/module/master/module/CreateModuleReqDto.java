package com.monaum.Rapid_Global.module.master.module;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateModuleReqDto {

    @NotBlank(message = "Module name is required.")
    private String name;

    @NotBlank(message = "Route is required.")
    private String route;

    private Integer sqnce;

    @NotNull(message = "Company ID is required.")
    private Long companyId;
}
