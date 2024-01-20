package com.group3.ezquiz.model.mapper;

import com.group3.ezquiz.model.User;
import com.group3.ezquiz.model.dto.UserDTO;

public class UserMapper {

    public User toUserEntity(UserDTO userDTO) {
        return null;
    }

    public static UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .role(user.getRole())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .fullName(user.getFullName())
                .note(user.getNote())
                .isVerified(user.getIsVerified())
                .isEnable(user.getIsEnable())
            .build();
    }
}
