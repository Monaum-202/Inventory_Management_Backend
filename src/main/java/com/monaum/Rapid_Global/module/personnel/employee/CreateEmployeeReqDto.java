package com.monaum.Rapid_Global.module.personnel.employee;

import jakarta.validation.constraints.*;
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
public class CreateEmployeeReqDto {

    @NotBlank(message = "Employee name is required.")
    private String name;

    @Email(message = "Invalid email format.")
    private String email;

    @NotBlank(message = "Phone number is required.")
    private String phone;

    @NotNull(message = "Monthly salary is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than zero.")
    private BigDecimal monthlySalary;

    @NotNull(message = "Joining date is required.")
    private LocalDate joiningDate;

    private Boolean active = true;

    @NotNull(message = "Company ID is required.")
    private Long companyId;
}
