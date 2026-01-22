package com.monaum.Rapid_Global.module.master.permission;

import com.monaum.Rapid_Global.module.master.menu.Menu;
import com.monaum.Rapid_Global.module.master.module.Module;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 21-Jan-26 9:54 PM
 */

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    @Mapping(source = "sqnce", target = "sequence")
    SidebarMenuDTO toDto(Menu entity);
    @Mapping(source = "sqnce", target = "sequence")
    SidebarModuleDTO toDto(Module entity);

    List<SidebarMenuDTO> toDtoList(List<Menu> entities);
}
