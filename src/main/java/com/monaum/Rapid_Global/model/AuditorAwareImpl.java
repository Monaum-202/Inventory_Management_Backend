package com.monaum.Rapid_Global.model;

import com.monaum.Rapid_Global.module.personnel.user.User;
import com.monaum.Rapid_Global.module.personnel.user.UserRepo;
import com.monaum.Rapid_Global.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<User> {

    @Autowired
    private UserRepo userRepo;

    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return Optional.empty();
        }

        String username = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
        return userRepo.findByUserNameIgnoreCase(username);
    }
}
