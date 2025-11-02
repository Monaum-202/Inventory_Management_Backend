package com.monaum.Rapid_Global.module.master.paymentMethod;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monaum.Rapid_Global.model.AbstractModel;
import com.monaum.Rapid_Global.module.master.company.Company;
import jakarta.persistence.*;
import lombok.*;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since oct 21, 2025
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_method")
public class PaymentMethod extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Integer sqn;
}
