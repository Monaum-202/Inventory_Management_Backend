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

            List<Menu> menus = menuRepository.findByRoleAndModule(roleId, module.getId());

            moduleDTO.setMenus(mapper.toDtoList(menus));

            return moduleDTO;

        }).toList();

        return ResponseUtils.SuccessResponseWithData(sidebarModules);
    }

    public ResponseEntity<BaseApiResponseDTO<?>> assignPermissions(RolePermissionRequestDTO dto) {

        roleModuleRepo.deleteByRoleId(dto.getRoleId());
        roleMenuRepo.deleteByRoleId(dto.getRoleId());

        dto.getModuleIds().stream().distinct().forEach(moduleId -> roleModuleRepo.save(RoleModule.builder().roleId(dto.getRoleId()).moduleId(moduleId).build()));

        dto.getMenuIds().stream().distinct().forEach(menuId -> roleMenuRepo.save(RoleMenu.builder().roleId(dto.getRoleId()).menuId(menuId).build()));

        return ResponseUtils.SuccessResponseWithData(dto);
    }


    public ResponseEntity<BaseApiResponseDTO<?>> getAssignedPermissions(Long roleId) {

        List<Module> modules = moduleRepository.findByRoleId(roleId);

        List<SidebarModuleDTO> result = modules.stream().map(module -> {

            SidebarModuleDTO dto = mapper.toDto(module);

            List<Menu> menus = menuRepository.findByRoleAndModule(roleId, module.getId());

            dto.setMenus(mapper.toDtoList(menus));
            return dto;

        }).toList();

        return ResponseUtils.SuccessResponseWithData(result);
    }

}
