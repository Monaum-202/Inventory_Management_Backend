package com.monaum.Rapid_Global.module.personnel.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monaum.Rapid_Global.model.AbstractModel;
import com.monaum.Rapid_Global.module.expenses.expense.Expense;
import com.monaum.Rapid_Global.module.master.company.Company;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
/**
 * Monaum Hossain
 * @since oct 21, 2025
 */

@Data
@Entity
@Table(name = "employeesggg")
@EqualsAndHashCode(onlyExplicitlyIncluded = false, callSuper = false)
public class Employee extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column( precision = 38, scale = 2)
    private BigDecimal salary;

    private Integer status;


    private LocalDate joiningDate;

    @Column(length = 50)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(length = 100)
    private String name;

}
