package com.monaum.Rapid_Global.config;

import com.monaum.Rapid_Global.module.personnel.user.User;
import com.monaum.Rapid_Global.module.personnel.user.UserRepo;
import com.monaum.Rapid_Global.security.UserDetailsImpl;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAware")
public class DataAuditorAware implements AuditorAware<User> {

	private final UserRepo userRepository;

	public DataAuditorAware(UserRepo userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Optional<User> getCurrentAuditor() {
		UserDetailsImpl user = getLoggedInUserDetails();
		if (user == null) return Optional.empty();
		return userRepository.findById(user.getId());
	}

	private UserDetailsImpl getLoggedInUserDetails() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) return null;

		if (!(auth.getPrincipal() instanceof UserDetailsImpl)) return null;

		return (UserDetailsImpl) auth.getPrincipal();
	}
}

