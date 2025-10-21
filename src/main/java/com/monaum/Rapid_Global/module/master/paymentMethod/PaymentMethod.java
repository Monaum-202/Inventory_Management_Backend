package com.monaum.Rapid_Global.module.master.paymentMethod;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monaum.Rapid_Global.model.AbstractModel;
import com.monaum.Rapid_Global.module.master.company.Company;
import jakarta.persistence.*;
import lombok.*;

/**
 * Monaum Hossain
 * @since oct 21, 2025
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_methods")
public class PaymentMethod extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g. "Cash", "Bank Transfer", "Bkash", "Card"

    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}
