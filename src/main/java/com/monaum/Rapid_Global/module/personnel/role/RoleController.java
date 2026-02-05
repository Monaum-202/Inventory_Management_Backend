package com.monaum.Rapid_Global.module.personnel.role;

import com.monaum.Rapid_Global.util.response.BaseApiResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 23-Nov-25 9:33 PM
 */

@RestController
@RequestMapping("/api/roles")
@AllArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
public class RoleController {

    private final RoleService service;

    @GetMapping
    public ResponseEntity<BaseApiResponseDTO<?>> getAll(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.getAll(pageable);
    }

    @PostMapping
    public ResponseEntity<BaseApiResponseDTO<?>> create(@RequestBody Role role) {
        return service.create(role);
    }

}
