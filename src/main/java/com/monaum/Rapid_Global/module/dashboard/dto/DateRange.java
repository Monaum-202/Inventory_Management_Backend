package com.monaum.Rapid_Global.module.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Supporting classes
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateRange {
    private LocalDate startDate;
    private LocalDate endDate;
}
