package com.monaum.Rapid_Global.module.personnel.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monaum.Rapid_Global.model.AbstractModel;
import com.monaum.Rapid_Global.module.expenses.expense.Expense;
import com.monaum.Rapid_Global.module.personnel.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 29-Oct-25 9:49 PM
 */

@Data
@Entity
@Table(name = "employee")
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "employee_id", unique = true)
    private String employeeId;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "phone", length = 50, unique = true)
    private String phone;

    @Column(name = "salary", precision = 18, scale = 2)
    private BigDecimal salary;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Column(name = "sqn")
    private Integer sqn;

    @Column(name = "active")
    private Boolean active = true;

}
