package com.monaum.Rapid_Global.module.master.unit;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitResDto {

    private Long id;
    private String name;
    private String shortName;
    private Boolean status;
    private Integer sqn;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

