package com.monaum.Rapid_Global.module.master.permission;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 21-Jan-26 9:52 PM
 */

@Entity
@Table(
        name = "ROLE_MENU",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ROLE_ID", "MENU_ID"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ROLE_ID", nullable = false)
    private Long roleId;

    @Column(name = "MENU_ID", nullable = false)
    private Long menuId;
}
