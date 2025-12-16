package com.monaum.Rapid_Global.module.personnel.role;

import com.monaum.Rapid_Global.module.personnel.user.UserResDto;
import com.monaum.Rapid_Global.util.PaginationUtil;
import com.monaum.Rapid_Global.util.ResponseUtils;
import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import com.monaum.Rapid_Global.util.response.CustomPageResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 23-Nov-25 10:08 PM
 */

@Service
public class RoleService {

    @Autowired RoleRepo repo;

    public ResponseEntity<BaseApiResponseDTO<?>> getAll( Pageable pageable) {
        Page<Role> roles = repo.findAllExcept(pageable);

        CustomPageResponseDTO<Role> paginatedResponse = PaginationUtil.buildPageResponse(roles, pageable);

        return ResponseUtils.SuccessResponseWithData(paginatedResponse);
    }

    public void initializeSystemRoles() {
        createRole(1L, "ROLE_ADMIN", "System Administrator");
        createRole(2L, "ROLE_SUPER_ADMIN", "Super Administrator");
    }

    private void createRole(Long id, String name, String description) {
        if (repo.existsByName(name)) {
            return;
        }

        Role role = new Role();
        role.setId(id);
        role.setName(name);
        role.setDescription(description);

        repo.save(role);
    }
}
