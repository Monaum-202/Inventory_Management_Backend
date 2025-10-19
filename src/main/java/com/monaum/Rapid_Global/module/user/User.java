package com.monaum.Rapid_Global.module.user;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monaum.Rapid_Global.model.AbstractModel;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class User extends AbstractModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@ToString.Include
	@Column(name = "user_name", length = 50, unique = true, nullable = false)
	private String userName;

	@Column(name = "email", length = 255, unique = true, nullable = false)
	private String email;

	@JsonIgnore
	@Column(name = "password", length = 100, nullable = false)
	private String password;

	@Column(name = "first_name", length = 50)
	private String firstName;

	@Column(name = "last_name", length = 50)
	private String lastName;

	@Column(name = "is_active", length = 1, nullable = false, columnDefinition = "BIT DEFAULT 0")
	private Boolean isActive;

	@Column(name = "country", length = 25)
	private String country;

	@Column(name = "phone", length = 25)
	private String phone;

	@Column(name = "location", length = 25)
	private String location;

	@Temporal(TemporalType.DATE)
	@Column(name = "date_of_birth")
	private Date dateOfBirth;

	@JsonIgnore
	@Lob
	@Column(name = "thumbnail")
	private byte[] thumbnail;
}
