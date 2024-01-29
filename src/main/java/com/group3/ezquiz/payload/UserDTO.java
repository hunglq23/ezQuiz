package com.group3.ezquiz.payload;

import com.group3.ezquiz.model.Role;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private Role role;
    private String email;
    private String fullName;
    private Boolean isEnable;
    private Boolean isVerified;
    private String phone;
    private String avatar;
    private String note;

    @Override
    public String toString() {
        return "UserDTO{" +
                "role=" + role +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", isEnable=" + isEnable +
                ", isVerified=" + isVerified +
                ", phone='" + phone + '\'' +
                ", avatar='" + avatar + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}