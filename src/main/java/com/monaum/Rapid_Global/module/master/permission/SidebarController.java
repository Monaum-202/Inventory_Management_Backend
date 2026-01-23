package com.monaum.Rapid_Global.module.master.permission;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.security.UserDetailsImpl;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * @since 22-Jan-26 11:08 PM
 */

@RestApiController
@RequestMapping("/api/sidebar")
@RequiredArgsConstructor
public class SidebarController {

    private final SidebarService sidebarService;

    @GetMapping
    public ResponseEntity<BaseApiResponseDTO<?>> getSidebar(@AuthenticationPrincipal UserDetailsImpl principal) {

        if (principal == null || principal.getUser() == null || principal.getUser().getRole() == null) {

            return ResponseUtils.FailedResponse("Unauthorized");
        }

        Long roleId = principal.getUser().getRole().getId();
        return sidebarService.getSidebar(roleId);
    }


    @PostMapping
    public ResponseEntity<BaseApiResponseDTO<?>> assignPermissions(@RequestBody RolePermissionRequestDTO request) {

        return sidebarService.assignPermissions(request);

    }

    @GetMapping("/{roleId}")
    public ResponseEntity<BaseApiResponseDTO<?>> getAssignedPermissions(@PathVariable Long roleId) {

        return sidebarService.getAssignedPermissions(roleId);
    }
}
