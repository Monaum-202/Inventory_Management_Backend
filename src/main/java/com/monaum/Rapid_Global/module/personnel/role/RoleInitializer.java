package com.monaum.Rapid_Global.module.personnel.role;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RoleInitializer {

    private final RoleService roleService;

    @PostConstruct
    public void init() {
        roleService.initializeSystemRoles();
    }
}
