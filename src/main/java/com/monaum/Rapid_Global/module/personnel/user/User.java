package com.monaum.Rapid_Global.module.personnel.user;

import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monaum.Rapid_Global.model.AbstractModel;
import com.monaum.Rapid_Global.module.master.company.Company;
import com.monaum.Rapid_Global.module.personnel.role.Role;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
//@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class User{

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

	@Column(name = "full_name", length = 50)
	private String fullName;

	@Column(name = "is_active", length = 1, nullable = false, columnDefinition = "BIT DEFAULT 0")
	private Boolean isActive = true;

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

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id")
	private Role role;

}
