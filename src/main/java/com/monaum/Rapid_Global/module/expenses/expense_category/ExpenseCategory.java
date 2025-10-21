package com.monaum.Rapid_Global.module.expenses.expense_category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monaum.Rapid_Global.model.AbstractModel;
import com.monaum.Rapid_Global.module.master.company.Company;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
/**
 * Monaum Hossain
 * @since oct 21, 2025
 */

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EXPENSE_CATEGORY")
@EqualsAndHashCode(callSuper = false)
public class ExpenseCategory extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g., Salary, Rent, Utilities

    private String description;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

}
