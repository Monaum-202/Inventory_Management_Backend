package com.monaum.Rapid_Global.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.monaum.Rapid_Global.module.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true) // Safe version
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AbstractModel implements Serializable {

	@JsonIgnore
	@CreatedBy
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", updatable = false)
	private User createdBy;

	@JsonIgnore
	@LastModifiedBy
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by")
	private User updatedBy;

	@CreatedDate
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

}
