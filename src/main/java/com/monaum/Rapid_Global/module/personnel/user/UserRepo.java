package com.monaum.Rapid_Global.module.personnel.user;

import java.util.List;
import java.util.Optional;

import com.monaum.Rapid_Global.module.personnel.role.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	List<User> findAllByIdIn(List<Long> ids);

	Optional<User> findByEmailIgnoreCase(String email);

	Optional<User> findByUserNameIgnoreCase(String userName);

	Optional<User> findByUserName(String userName);

	boolean existsByUserName(String userName);

	boolean existsByEmail(String email);

	@Query("SELECT u FROM User u WHERE " +
			"LOWER(u.userName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
			"LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
			"LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
			"LOWER(u.phone) LIKE LOWER(CONCAT('%', :search, '%'))")
	Page<User> search(@Param("search") String search, Pageable pageable);

	@Query("SELECT r FROM User r WHERE r.role.id <> 1")
	Page<User> findAllExcept(Pageable pageable);
}
