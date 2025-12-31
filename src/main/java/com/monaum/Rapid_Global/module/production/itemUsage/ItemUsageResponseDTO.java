package com.monaum.Rapid_Global.module.production.itemUsage;

import com.monaum.Rapid_Global.enums.Status;
import com.monaum.Rapid_Global.module.production.item.ItemResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemUsageResponseDTO {

    private Long id;

    private LocalDate date;

    private Status status;

    private List<ItemResponseDTO> items;
}
