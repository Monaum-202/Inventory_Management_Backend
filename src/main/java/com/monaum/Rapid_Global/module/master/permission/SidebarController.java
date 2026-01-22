package com.monaum.Rapid_Global.module.master.permission;

import com.monaum.Rapid_Global.annotations.RestApiController;
import com.monaum.Rapid_Global.security.UserDetailsImpl;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;


import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 22-Jan-26 11:08 PM
 */

@RestApiController
@RequestMapping("/api/sidebar")
@RequiredArgsConstructor
public class SidebarController {

    private final SidebarService sidebarService;

    @GetMapping
    public ResponseEntity<BaseApiResponseDTO<?>> getSidebar(
            @AuthenticationPrincipal UserDetailsImpl principal) {

        return sidebarService.getSidebar(principal.getUser().getRole().getId());
    }


    @PostMapping
    public ResponseEntity<Void> assignPermissions(
            @RequestBody RolePermissionRequestDTO request) {

        sidebarService.assignPermissions(request);
        return ResponseEntity.ok().build();
    }
}
