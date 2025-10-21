package com.monaum.Rapid_Global.module.personnel.user;

import java.util.Optional;

import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepo usersRepo;

	@Override
	public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {

		if (input == null || input.isBlank()) throw new CustomException("Username or email is required", HttpStatus.BAD_REQUEST);

		Optional<User> userOp = usersRepo.findByUserNameIgnoreCase(input);

		if (userOp.isEmpty()) {
			userOp = usersRepo.findByEmailIgnoreCase(input);
		}

		if (userOp.isEmpty()) throw new CustomException("User not found with username/email: " + input, HttpStatus.NOT_FOUND);

		User user = userOp.get();

		if (Boolean.FALSE.equals(user.getIsActive())) throw new CustomException("User is inactive", HttpStatus.UNAUTHORIZED);

		return new UserDetailsImpl(user);
	}
}
