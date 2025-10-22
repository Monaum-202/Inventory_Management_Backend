package com.monaum.Rapid_Global.module.master.menu;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMenuReqDto {

    @NotNull(message = "Menu ID is required.")
    private Long id;

    @NotBlank(message = "Menu name is required.")
    private String name;

    @NotNull(message = "Module ID is required.")
    private Long moduleId;

    @NotBlank(message = "Route is required.")
    private String route;

    private Integer sqnce;

    @NotNull(message = "Company ID is required.")
    private Long companyId;
}

