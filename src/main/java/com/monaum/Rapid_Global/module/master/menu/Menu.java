package com.monaum.Rapid_Global.module.master.menu;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monaum.Rapid_Global.model.AbstractModel;
import com.monaum.Rapid_Global.module.master.company.Company;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MENU")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", length = 50, nullable = false)
    private String name;

    @Column(name = "MODULE_ID", nullable = false)
    private Long moduleId;

    @Column(name = "ROUTE", length = 100, nullable = false)
    private String route;

    @Column(name = "SEQUENCE")
    private Integer sqnce;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}
