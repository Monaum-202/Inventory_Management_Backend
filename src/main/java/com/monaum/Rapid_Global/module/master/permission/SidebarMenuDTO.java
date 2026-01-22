package com.monaum.Rapid_Global.module.master.permission;

import lombok.Builder;
import lombok.Data;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 21-Jan-26 9:53 PM
 */

@Data
@Builder
public class SidebarMenuDTO {
    private Long id;
    private String name;
    private String route;
    private Integer sequence;
}
