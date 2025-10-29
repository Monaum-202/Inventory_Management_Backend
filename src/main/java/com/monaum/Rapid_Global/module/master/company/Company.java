package com.monaum.Rapid_Global.module.master.company;

import com.monaum.Rapid_Global.model.AbstractModel;
import jakarta.persistence.*;
import lombok.*;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "COMPANY")
@EqualsAndHashCode(callSuper = true)
public class Company extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = false , length = 50)
    private String name;

    @Column(name = "ADDRESS", length = 100)
    private String address;

    @Column(name = "PHONE", nullable = false, length = 20)
    private String phone;

    @Column(name = "EMAIL" , length = 100)
    private String email;

    @Column(name = "STATUS")
    private Boolean status;

}
