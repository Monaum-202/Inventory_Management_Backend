package com.monaum.Rapid_Global.module.master.permission;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 21-Jan-26 9:53 PM
 */

@Data
@Builder
public class SidebarModuleDTO {
    private Long id;
    private String name;
    private String route;
    private Integer sequence;
    private String icon;
    private List<SidebarMenuDTO> menus;
}
