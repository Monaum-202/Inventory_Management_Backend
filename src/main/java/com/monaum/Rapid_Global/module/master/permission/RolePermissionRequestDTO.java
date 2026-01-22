package com.monaum.Rapid_Global.module.master.permission;

import lombok.Data;

import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 22-Jan-26 11:27 PM
 */

@Data
public class RolePermissionRequestDTO {

    private Long roleId;
    private List<Long> moduleIds;
    private List<Long> menuIds;
}
