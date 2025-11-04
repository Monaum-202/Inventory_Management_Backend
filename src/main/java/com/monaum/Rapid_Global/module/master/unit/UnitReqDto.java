package com.monaum.Rapid_Global.module.master.unit;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitReqDto {

    @NotBlank(message = "Unit name is required.")
    private String name;

    @NotBlank(message = "Short name is required.")
    private String shortName;

    private Integer sqn;
}