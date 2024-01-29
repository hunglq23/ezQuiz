package com.group3.ezquiz.payload;

import com.group3.ezquiz.model.Role;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private Role role;
    private String email;
    private String password;
    private String fullName;
    private Boolean isEnable;
    private Boolean isVerified;
    private String phone;
    private String note;

}