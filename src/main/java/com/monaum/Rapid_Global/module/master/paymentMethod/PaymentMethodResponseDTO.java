package com.monaum.Rapid_Global.module.master.paymentMethod;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 25-Oct-25 10:32 PM
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
