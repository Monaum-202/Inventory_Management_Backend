package com.monaum.Rapid_Global.module.master.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyResDto {

    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private Boolean status;

}


