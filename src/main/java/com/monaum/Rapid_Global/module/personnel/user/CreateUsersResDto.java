package com.monaum.Rapid_Global.module.personnel.user;

import org.springframework.beans.BeanUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Zubayer Ahamed
 * @since Jul 2, 2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUsersResDto {

	private Long id;
	private String email;
	private String firstName;
	private String lastName;
	private Boolean isActive;

	public CreateUsersResDto(User users) {
		BeanUtils.copyProperties(users, this);
	}
}
