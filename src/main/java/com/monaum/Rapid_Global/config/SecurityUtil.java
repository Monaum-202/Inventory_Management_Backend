package com.monaum.Rapid_Global.config;

import com.monaum.Rapid_Global.module.user.User;
import com.monaum.Rapid_Global.module.user.UserRepo;
import com.monaum.Rapid_Global.security.UserDetailsImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepo userRepo;
    private static UserRepo staticUserRepo;

    @PostConstruct
    public void init() {
        staticUserRepo = userRepo;
    }

    public Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            return Optional.of(staticUserRepo.getReferenceById(userDetails.getId()));
        }

        return Optional.empty();
    }

    protected User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            throw new UsernameNotFoundException("User not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Retrieve the user by their username (from the token)
        return userRepo.findByUserNameIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userDetails.getUsername()));
    }


}
