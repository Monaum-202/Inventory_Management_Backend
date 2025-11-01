package com.monaum.Rapid_Global.config;

import java.util.Optional;

import com.monaum.Rapid_Global.security.UserDetailsImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * Zubayer Ahamed
 * @since Jun 22, 2025
 */
public class DataAuditorAware implements AuditorAware<Long> {

	@Override
	public @NonNull Optional<Long> getCurrentAuditor() {
		try {
			UserDetailsImpl user = getLoggedInUserDetails();
			if (user != null && user.getId() != null) {
				return Optional.of(user.getId());
			}
		} catch (Exception e) {

		}
		return Optional.of(0L); // fallback
	}


	public UserDetailsImpl getLoggedInUserDetails() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth == null || !auth.isAuthenticated()) return null;

		Object principal = auth.getPrincipal();
		if(!(principal instanceof UserDetailsImpl)) return null;
		return (UserDetailsImpl) principal;
	}
}
