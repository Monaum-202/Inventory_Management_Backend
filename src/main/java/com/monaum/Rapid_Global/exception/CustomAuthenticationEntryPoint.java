package com.monaum.Rapid_Global.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

		ErrorResponse error = ErrorResponse.builder()
				.status(HttpStatus.UNAUTHORIZED.value())
				.error("Unauthorized")
				.message("Authentication is required to access this resource")
				.build();

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json");
		mapper.writeValue(response.getWriter(), error);
	}
}
