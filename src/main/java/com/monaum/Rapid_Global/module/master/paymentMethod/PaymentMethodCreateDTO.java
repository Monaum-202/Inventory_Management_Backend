package com.monaum.Rapid_Global.module.master.paymentMethod;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 25-Oct-25 10:32 PM
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodCreateDTO {

    @NotBlank(message = "Payment method name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    private Boolean active = true;
}
