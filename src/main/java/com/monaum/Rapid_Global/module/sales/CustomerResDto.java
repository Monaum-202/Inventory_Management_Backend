package com.monaum.Rapid_Global.module.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResDto {

    private Long id;
    private String name;
    private String phone;
    private Long altPhone;
    private String email;
    private String address;
    private String businessAddress;
    private BigDecimal totalTransaction;
    private Long companyId;
    private LocalDateTime createdAt;
}
