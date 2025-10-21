package com.monaum.Rapid_Global.security.tokens;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Repository
public interface TokenRepo extends JpaRepository<Token, Long> {

	List<Token> findAllByUserIdAndRevokedAndExpired(Long userId, boolean revoked, boolean expired);
	Optional<Token> findByToken(String token);
}
