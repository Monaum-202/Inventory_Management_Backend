package com.monaum.Rapid_Global.security.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationReqDto {

	private String login;
	private String password;
}
