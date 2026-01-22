package com.monaum.Rapid_Global.module.master.permission;

import com.monaum.Rapid_Global.module.master.menu.Menu;
import com.monaum.Rapid_Global.module.master.menu.MenuRepo;
import com.monaum.Rapid_Global.module.master.module.Module;
import com.monaum.Rapid_Global.module.master.module.ModuleRepo;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final RoleModuleRepository roleModuleRepo;
    private final RoleMenuRepository roleMenuRepo;

    public ResponseEntity<BaseApiResponseDTO<?>> getSidebar(Long roleId) {

        List<Module> modules = moduleRepository.findByRoleId(roleId);

        List<SidebarModuleDTO> sidebarModules = modules.stream().map(module -> {

            SidebarModuleDTO moduleDTO = mapper.toDto(module);

            List<Menu> menus =
                    menuRepository.findByRoleAndModule(roleId, module.getId());

            moduleDTO.setMenus(mapper.toDtoList(menus));

            return moduleDTO;

        }).toList();

        return ResponseUtils.SuccessResponseWithData(sidebarModules);
    }

    public void assignPermissions(RolePermissionRequestDTO dto) {

        // 1️⃣ Remove old permissions
        roleModuleRepo.deleteByRoleId(dto.getRoleId());
        roleMenuRepo.deleteByRoleId(dto.getRoleId());

        // 2️⃣ Insert module permissions
        dto.getModuleIds().forEach(moduleId ->
                roleModuleRepo.save(
                        RoleModule.builder()
                                .roleId(dto.getRoleId())
                                .moduleId(moduleId)
                                .build()
                )
        );

        // 3️⃣ Insert menu permissions
        dto.getMenuIds().forEach(menuId ->
                roleMenuRepo.save(
                        RoleMenu.builder()
                                .roleId(dto.getRoleId())
                                .menuId(menuId)
                                .build()
                )
        );
    }
}
