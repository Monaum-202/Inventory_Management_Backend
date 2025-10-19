package com.monaum.Rapid_Global.module.auth;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.monaum.Rapid_Global.enums.TokenType;
import com.monaum.Rapid_Global.exception.CustomException;
import com.monaum.Rapid_Global.security.JwtService;
import com.monaum.Rapid_Global.module.tokens.Token;
import com.monaum.Rapid_Global.module.tokens.TokenRepo;
import com.monaum.Rapid_Global.module.user.User;
import com.monaum.Rapid_Global.module.user.UserRepo;
import com.monaum.Rapid_Global.module.user.UserService;
import com.monaum.Rapid_Global.security.UserDetailsImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Service
public class AuthenticationService {


	@Autowired private UserRepo userRepo;
	@Autowired private JwtService jwtService;
	@Autowired PasswordEncoder passwordEncoder;
	@Autowired private TokenRepo tokensRepo;
	@Autowired private UserService userService;

	@Transactional
	public AuthenticationResDto register(RegisterRequestDto request) {
		// 1. Check if user already exists
		if (userRepo.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
			throw new CustomException("Email is already registered.", HttpStatus.BAD_REQUEST);
		}else if (userRepo.findByUserNameIgnoreCase(request.getUserName()).isPresent()) {
			throw new CustomException("Username is already registered.", HttpStatus.BAD_REQUEST);
		}

		// 2. Create user
		User user = User.builder()
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.email(request.getEmail())
				.userName(request.getUserName())
				.password(passwordEncoder.encode(request.getPassword()))
				.isActive(Boolean.TRUE).build();

		user = userRepo.save(user);

		// 3. Generate JWT token and Refresh token
		var jwtToken = jwtService.generateToken(new UserDetailsImpl(user));
		var refreshToken = jwtService.generateRefreshToken(new UserDetailsImpl(user));

		// 4. Save User Token
		saveUserToken(user.getId(), jwtToken);

		// 5. Return token
		return AuthenticationResDto.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
	}

	@Transactional
	public AuthenticationResDto authenticate(AuthenticationReqDto request) {
		// 1. Find user by username or email
		Optional<User> userOp = userRepo.findByUserNameIgnoreCase(request.getLogin());
		if (userOp.isEmpty()) userOp = userRepo.findByEmailIgnoreCase(request.getLogin());

		System.out.println("User found: " + userOp.isPresent());
		User user = userOp.orElseThrow(() -> new RuntimeException("User not found with provided username/email."));

		// 2. Check if user is active
		if (Boolean.FALSE.equals(user.getIsActive())) {
			throw new RuntimeException("User account is inactive.");
		}

		// 3. Verify password
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new RuntimeException("Invalid credentials.");
		}

		// 4. Generate token
		var jwtToken = jwtService.generateToken(new UserDetailsImpl(user));
		var refreshToken = jwtService.generateRefreshToken(new UserDetailsImpl(user));

		// 5. Revoke tokens and save new token
		revokeAllUserTokens(user.getId());
		saveUserToken(user.getId(), jwtToken);

		// 6. Return token
		return AuthenticationResDto.builder()
				.accessToken(jwtToken)
				.refreshToken(refreshToken)
				.build();
	}


	@Transactional
	void revokeAllUserTokens(Long zuser) {
		List<Token> validTokens = tokensRepo.findAllByUserIdAndRevokedAndExpired(zuser, false, false);
		if (validTokens.isEmpty())
			return;

		validTokens.forEach(t -> {
			t.setRevoked(true);
			t.setExpired(true);
		});

		tokensRepo.saveAll(validTokens);
	}

	@Transactional
	void saveUserToken(Long zuser, String jwtToken) {
		Token xtoken = Token.builder()
				.userId(zuser)
				.token(jwtToken)
				.revoked(false)
				.expired(false)
				.xtype(TokenType.BEARER)
				.build();

		tokensRepo.save(xtoken);
	}

	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String refreshToken;
		final String usernameOrEmail;

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return;
		}

		refreshToken = authHeader.substring(7);
		usernameOrEmail = jwtService.extractUsername(refreshToken); // contains either username or email

		if (StringUtils.isNotBlank(usernameOrEmail)) {
			UserDetailsImpl userDetails = (UserDetailsImpl) userService.loadUserByUsername(usernameOrEmail);

			if (jwtService.isTokenValid(refreshToken, userDetails)) {
				String accessToken = jwtService.generateToken(userDetails);
				Long userId = userDetails.getUser().getId();

				revokeAllUserTokens(userId);
				saveUserToken(userId, accessToken);

				AuthenticationResDto authResponse = AuthenticationResDto.builder()
						.accessToken(accessToken)
						.refreshToken(refreshToken)
						.build();

				new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
			}
		}
	}


}
