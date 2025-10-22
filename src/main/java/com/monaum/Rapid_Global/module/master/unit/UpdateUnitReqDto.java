package com.monaum.Rapid_Global.module.master.unit;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUnitReqDto {

    @NotNull(message = "Unit ID is required.")
    private Long id;

    @NotBlank(message = "Unit name is required.")
    private String name;

    @NotBlank(message = "Short name is required.")
    private String shortName;

    @NotNull(message = "Company ID is required.")
    private Long companyId;

    private Boolean status;

    private Integer sortingOrder;
}

