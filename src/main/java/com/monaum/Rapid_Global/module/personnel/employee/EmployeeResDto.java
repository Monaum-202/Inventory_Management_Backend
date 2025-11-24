package com.monaum.Rapid_Global.module.personnel.employee;

import com.monaum.Rapid_Global.module.expenses.expense.ExpenseResDto;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 29-Oct-25 9:49 PM
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResDto {

    private Long id;
    private String employeeId;
    private String name;
    private String email;
    private String phone;
    private BigDecimal salary;
    private LocalDate joiningDate;
    private Boolean active;
    private Integer sqn;

//    private List<ExpenseResDto> lends;
    private BigDecimal totalLend;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
