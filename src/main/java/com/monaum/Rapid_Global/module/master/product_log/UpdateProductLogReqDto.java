package com.monaum.Rapid_Global.module.master.product_log;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductLogReqDto {

    @NotNull(message = "Product log ID is required.")
    private Long id;

    @NotNull(message = "Product ID is required.")
    private Long productId;

    @NotNull(message = "Changed by user ID is required.")
    private Long changedBy;

    @Size(max = 50, message = "Old name cannot exceed 50 characters.")
    private String oldName;

    @Size(max = 50, message = "New name cannot exceed 50 characters.")
    private String newName;

    @Size(max = 255, message = "Old description cannot exceed 255 characters.")
    private String oldDescription;

    @Size(max = 255, message = "New description cannot exceed 255 characters.")
    private String newDescription;

    private Long oldUnitId;
    private Long newUnitId;

    @Digits(integer = 8, fraction = 2, message = "Old price per unit must be a valid decimal number (max 10,2).")
    private BigDecimal oldPricePerUnit;

    @Digits(integer = 8, fraction = 2, message = "New price per unit must be a valid decimal number (max 10,2).")
    private BigDecimal newPricePerUnit;

    private Integer oldStatus;
    private Integer newStatus;

    @NotNull(message = "Change type is required.")
    private Integer changeType;

    @NotNull(message = "Change date and time are required.")
    private LocalDateTime changedAt;

    @NotNull(message = "Company ID is required.")
    private Long companyId;
}

