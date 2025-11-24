package com.monaum.Rapid_Global.module.personnel.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReqDTO {

    @NotBlank(message = "Username is required")
    @Size(max = 50, message = "Username must not exceed 50 characters")
    private String userName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String fullName;

    @Size(max = 25, message = "Country must not exceed 25 characters")
    private String country;

    @Size(max = 25, message = "Phone must not exceed 25 characters")
    private String phone;

    @Size(max = 25, message = "Location must not exceed 25 characters")
    private String location;

    private Date dateOfBirth;

    private byte[] thumbnail;

    @NotNull(message = "Role ID is required")
    private Long roleId;
}