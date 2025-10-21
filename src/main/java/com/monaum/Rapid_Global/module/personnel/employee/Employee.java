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

    @Column(nullable = false)
    private String name;

    private String email;

    private String phone;

    @Column(nullable = false)
    private BigDecimal monthlySalary;

    private boolean active = true;

    private LocalDate joiningDate;

    @JsonIgnore
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> lends;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

}
