package com.monaum.Rapid_Global.module.master.menu;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuDto {

    private Long id;
    private String name;
    private Long moduleId;
    private String route;
    private Integer sqnce;
    private Long companyId;
}
