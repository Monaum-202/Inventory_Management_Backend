package com.monaum.Rapid_Global.module.master.unit;


import com.monaum.Rapid_Global.model.AbstractModel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "UNIT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Unit extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Column(name = "full_name", unique = true, length = 100)
    private String fullName;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "sqn")
    private Integer sqn;
}

