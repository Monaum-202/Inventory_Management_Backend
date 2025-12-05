
package com.monaum.Rapid_Global.module.incomes.customer;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerReqDto {

    @NotBlank(message = "Customer name is required.")
    @Size(max = 50, message = "Customer name can be at most 50 characters.")
    private String name;

    @NotBlank(message = "Phone is required.")
    @Pattern(
            regexp = "^[0-9]{10,15}$",
            message = "Phone must be 10–15 digits."
    )
    private String phone;

    @Pattern(
            regexp = "^[0-9]{10,15}$",
            message = "Alternate phone must be 10–15 digits."
    )
    private String altPhone;

    @Email(message = "Invalid email address.")
    @Size(max = 100, message = "Email can be at most 100 characters.")
    private String email;

    @Size(max = 150, message = "Address can be at most 150 characters.")
    private String address;

    @Size(max = 150, message = "Business name can be at most 150 characters.")
    private String companyName;

}
