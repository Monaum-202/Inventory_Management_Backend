package com.monaum.Rapid_Global.security.tokens;

import java.io.Serializable;

import com.monaum.Rapid_Global.enums.TokenType;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tokens")
public class Token implements Serializable {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String token;

	@Enumerated(EnumType.STRING)
	private TokenType xtype;

	@Column(name = "is_revoked")
	private boolean revoked;

	@Column(name = "is_expired")
	private boolean expired;

	@Column(name = "user_id")
	private Long userId;
}
