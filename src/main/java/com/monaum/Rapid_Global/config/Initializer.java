package com.monaum.Rapid_Global.config;

import com.monaum.Rapid_Global.module.master.transectionCategory.TransactionCategoryService;
import com.monaum.Rapid_Global.module.personnel.role.RoleService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class Initializer {

    private final RoleService roleService;
    private final TransactionCategoryService transactionCategory;

    @PostConstruct
    public void init() {
        roleService.initializeSystemRoles();
        transactionCategory.initializeSystemCategory();
    }
}
