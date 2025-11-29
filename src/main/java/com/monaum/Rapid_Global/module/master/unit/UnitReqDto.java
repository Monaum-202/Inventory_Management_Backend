package com.monaum.Rapid_Global.module.master.unit;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitReqDto {

    @NotBlank(message = "Unit name is required.")
    private String name;

    @NotBlank(message = "Full name is required.")
    private String fullName;

    private Integer sqn;
}