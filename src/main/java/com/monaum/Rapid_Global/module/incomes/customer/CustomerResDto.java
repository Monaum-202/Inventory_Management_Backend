package com.monaum.Rapid_Global.module.incomes.customer;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CustomerResDto {

    private Long id;

    private String name;

    private String phone;

    private String altPhone;

    private String email;

    private String address;

    private String businessName;

    private String businessAddress;

    private BigDecimal totalTransaction;

    private String createdAt;

    private String updatedAt;
}
