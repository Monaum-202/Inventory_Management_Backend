package com.monaum.Rapid_Global.module.personnel.user;

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
public class UserResDto {

    private Long id;
    private String userName;
    private String email;
    private String fullName;
    private Boolean isActive;
    private String country;
    private String phone;
    private String location;
    private Date dateOfBirth;
    private byte[] thumbnail;
    private Long roleId;
    private String roleName;
}