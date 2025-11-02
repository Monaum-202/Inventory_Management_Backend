package com.monaum.Rapid_Global.module.master.unit;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitResDto {

    private Long id;
    private String name;
    private String shortName;
    private Long companyId;
    private Boolean status;
    private Integer sortingOrder;
}

