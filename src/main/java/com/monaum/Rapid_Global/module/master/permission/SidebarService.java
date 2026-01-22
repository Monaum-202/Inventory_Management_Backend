package com.monaum.Rapid_Global.module.master.permission;

import com.monaum.Rapid_Global.module.master.menu.Menu;
import com.monaum.Rapid_Global.module.master.menu.MenuRepo;
import com.monaum.Rapid_Global.module.master.module.Module;
import com.monaum.Rapid_Global.module.master.module.ModuleRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 21-Jan-26 10:03 PM
 */

@Service
@RequiredArgsConstructor
@Transactional
public class SidebarService {
    private final ModuleRepo moduleRepository;
    private final MenuRepo menuRepository;
    private final PermissionMapper mapper;

    public List<SidebarModuleDTO> getSidebar(Long roleId) {

        List<Module> modules = moduleRepository.findByRoleId(roleId);

        return modules.stream().map(module -> {

            SidebarModuleDTO moduleDTO = mapper.toDto(module);

            List<Menu> menus = menuRepository.findByRoleAndModule(roleId, module.getId());

            moduleDTO.setMenus(mapper.toDtoList(menus));

            return moduleDTO;

        }).toList();
    }
}
