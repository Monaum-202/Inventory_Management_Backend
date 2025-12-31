package com.monaum.Rapid_Global.module.production.itemUsage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monaum.Rapid_Global.enums.Status;
import com.monaum.Rapid_Global.model.AbstractModel;
import com.monaum.Rapid_Global.module.production.item.Item;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 25-Dec-25 1:40 AM
 */

@Entity
@Table(name = "item_usage")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemUsage extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @JsonIgnore
    @OneToMany(mappedBy = "itemUsage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    @Column(name = "status")
    private Status status = Status.PENDING;
}
