package com.monaum.Rapid_Global.module.production.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseDTO {

    private Long id;

    private Long productId;

    private String itemName;

    private String unitName;

    private Integer quantity;

    private String note;
}
