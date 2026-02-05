package com.monaum.Rapid_Global.module.master.approval;

import com.monaum.Rapid_Global.model.AbstractModel;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 30-Jan-26 7:08 PM
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "APPROVAL_LEVEL")
public class ApprovalLevel extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String levelCode; // e.g., "MANAGER", "ACCOUNT_MANAGER", "MD"

    @Column(nullable = false)
    private String levelName; // e.g., "Manager", "Account Manager", "Managing Director"

    @Column(nullable = false)
    private Integer levelOrder; // 1, 2, 3 (determines approval sequence)

    @Column(nullable = false)
    private BigDecimal maxApprovalAmount; // Maximum amount this level can approve

    @Column(nullable = false)
    private Boolean canApproveUnlimited = false; // For MD or top level

    @Column(nullable = false)
    private Boolean isActive = true;

    private String description;
}
