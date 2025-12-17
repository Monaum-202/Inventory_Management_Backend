package com.monaum.Rapid_Global.module.incomes.customer;

import com.monaum.Rapid_Global.module.incomes.income.IncomeResDto;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CustomerResDto {

    private Long id;

    private String name;

    private String phone;

    private String altPhone;

    private String email;

    private String address;

    private String companyName;

    private String createdAt;

    private String updatedAt;

    private BigDecimal totalTransaction;
    private BigDecimal totalDue;
}
