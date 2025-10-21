package com.monaum.Rapid_Global.module.personnel.employee;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Monaum Hossain
 * @since Oct 21, 2025
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private BigDecimal monthlySalary;
    private LocalDate joiningDate;
    private Boolean active;
    private Long companyId;
    private String companyName;

    private List<ExpenseResDto> lends;
}
