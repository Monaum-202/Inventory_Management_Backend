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

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EMPLOYEES")
@EqualsAndHashCode(callSuper = false)
public class Employee extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", length = 100, nullable = false)
    private String name;

    @Column(name = "EMAIL", unique = true, length = 50)
    private String email;

    @Column(name = "PHONE", unique = true, length = 50)
    private String phone;

    @Column(name ="SALARY", nullable = false)
    private BigDecimal monthlySalary;

    private LocalDate joiningDate;

    @Column(name = "STATUS", nullable = false)
    private boolean status = true;

    @JsonIgnore
    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<Expense> lends;


}
