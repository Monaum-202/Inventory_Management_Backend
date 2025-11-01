package com.monaum.Rapid_Global.module.personnel.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	List<User> findAllByIdIn(List<Long> ids);

	Optional<User> findByEmailIgnoreCase(String email);

	Optional<User> findByUserNameIgnoreCase(String userName);
}
