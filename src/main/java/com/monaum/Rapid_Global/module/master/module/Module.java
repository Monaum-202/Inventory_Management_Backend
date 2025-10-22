package com.monaum.Rapid_Global.module.master.module;


import com.monaum.Rapid_Global.model.AbstractModel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MODULE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Module extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", length = 20, nullable = false)
    private String name;

    @Column(name = "ROUTE", length = 100, nullable = false)
    private String route;

    @Column(name = "SEQUENCE")
    private Integer sqnce;

    @Column(name = "COMPANY_ID", nullable = false)
    private Long companyId;
}
