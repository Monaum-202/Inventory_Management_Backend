package com.monaum.Rapid_Global.module.master.unit;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUnitReqDto {

    @NotBlank(message = "Unit name is required.")
    private String name;

    @NotBlank(message = "Short name is required.")
    private String shortName;

    @NotNull(message = "Company ID is required.")
    private Long companyId;

    private Boolean status = true;

    private Integer sortingOrder;
}