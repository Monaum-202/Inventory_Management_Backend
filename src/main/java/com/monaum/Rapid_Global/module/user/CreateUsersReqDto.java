package com.monaum.Rapid_Global.module.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Zubayer Ahamed
 * @since Jul 2, 2025
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUsersReqDto {

	private String email;
	private String userName;
	private String password;
	private String firstName;
	private String lastName;

	public User getBean() {
		return User.builder()
				.email(email)
				.userName(userName)
				.password(password)
				.firstName(firstName)
				.lastName(lastName)
				.build();
	}
}
