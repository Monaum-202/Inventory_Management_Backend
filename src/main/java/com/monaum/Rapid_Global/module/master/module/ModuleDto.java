package com.monaum.Rapid_Global.module.master.module;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleDto {

    private Long id;
    private String name;
    private String route;
    private Integer sqnce;
    private Long companyId;
}

