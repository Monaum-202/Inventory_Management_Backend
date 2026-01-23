package com.monaum.Rapid_Global.module.master.module;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * @since 23-Jan-26 6:08 PM
 */

import com.monaum.Rapid_Global.module.master.permission.PermissionMapper;
import com.monaum.Rapid_Global.module.master.menu.Menu;
import com.monaum.Rapid_Global.module.master.menu.MenuRepo;
import com.monaum.Rapid_Global.module.master.permission.SidebarModuleDTO;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import com.monaum.Rapid_Global.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/modules")
public class ModuleController {

    private final ModuleRepo moduleRepo;
    private final MenuRepo menuRepo;
    private final PermissionMapper mapper;

    @GetMapping("/with-menus")
    public ResponseEntity<BaseApiResponseDTO<?>> getModulesWithMenus() {
        List<Module> modules = moduleRepo.findAll();

        List<SidebarModuleDTO> dtos = modules.stream().map(module -> {
            SidebarModuleDTO dto = mapper.toDto(module);
            List<Menu> menus = menuRepo.findByModuleId(module.getId());
            dto.setMenus(mapper.toDtoList(menus));
            return dto;
        }).toList();

        return ResponseUtils.SuccessResponseWithData(dtos);
    }
}