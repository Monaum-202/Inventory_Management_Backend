package com.monaum.Rapid_Global.module.master.unit;


import com.monaum.Rapid_Global.model.AbstractModel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "UNIT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Unit extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", length = 100, nullable = false)
    private String name;

    @Column(name = "SHORT_NAME", unique = true, length = 20, nullable = false)
    private String shortName;

    @Column(name = "COMPANY_ID", nullable = false)
    private Long companyId;

    @Column(name = "STATUS", nullable = false)
    private Boolean status;

    @Column(name = "SORTING_ORDER")
    private Integer sortingOrder;
}

